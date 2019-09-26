(ns evaluation-app.services.evaluation
  (:require
   [reagent.core :as r]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [markdown.core :refer [md->html]]
   [evaluation-app.ajax :as ajax]
   [ajax.core :refer [GET POST PUT]]
   [reitit.core :as reitit]
   [clojure.string :as string])
  (:import goog.History))

(defonce datasets (r/atom []))
(defonce dataset (r/atom {}))
(defonce evaluate-dataset (r/atom {}))
(def init-evaluate-dataset {})
(def selected-dataset (r/atom nil))
(defonce evaluated-datasets (r/atom #{}))
(defonce state (r/atom :select-dataset))

(defn fetch-datasets! []
  (GET "/api/files/datasets" {:handler #(reset! datasets %)}))

(defn fetch-dataset! [filename]
  (GET "/api/files/dataset" {:params {:name filename}
                             :handler #(reset! dataset %)}))

(defn parse-date [date-str]
  (if (> (count date-str) 10)
    (str (subs date-str 0 4) "/" (subs date-str 4 6) "/" (subs date-str 6 8) "/"  (subs date-str 8 10) "." (subs date-str 10))))

(defn load-section []
  [:div.content
   [:ul
    (for [ds @datasets]
      [:li.box ^{:key (str ds)} {:style {:width "500px"}} [:div.columns [:div.column.is-8 [:p (str ds)]]
                                                           (if-not (contains? @evaluated-datasets (str ds))
                                                             [:div.column [:button.button.is-link
                                                                           {:on-click #(do
                                                                                         (reset! selected-dataset (str ds))
                                                                                         (fetch-dataset! (str ds))
                                                                                         (reset! state :view-dataset)
                                                                                         (reset! evaluate-dataset init-evaluate-dataset))}
                                                                           "evaluate it " [:i.material-icons "arrow_forward"]]]
                                                             [:div.column [:p.tag.is-light.is-medium "evaluated " [:i.material-icons "check"]]])]])]])

(defn article-info-section []
  (let [article-info (:article_info @dataset)]
    [:section.section>div.container
     [:div.title [:p "Article Info"]]
     [:table.table
      [:tbody
       [:tr [:th "title"] [:th (:article_title article-info)]]
       [:tr [:th "title (yomi) "] [:th (:article_title_yomi article-info)]]
       [:tr [:th "last update"] [:th (parse-date (:updated-date article-info))]]
       [:tr [:th "category"] [:th (:article_category article-info)]]
       [:tr [:th "source"] [:th @selected-dataset]]]]]))

(defn view-dataset []
  [:div
   [article-info-section]
   [:section.section>div.container
    [:div.column [:button.button.is-link
                  {:on-click #(do
                                (println  @evaluated-datasets)
                                (reset! state :evaluate-1-dataset))}
                  "1st evaluation " [:i.material-icons "arrow_forward"]]]
    [:div.column [:button.button.is-info
                  {:on-click #(do
                                (reset! state :select-dataset))}
                  "back to selection dataset " [:i.material-icons "cancel"]]]]])

(defn add-evaluate [idx task-key points]
  (swap! evaluate-dataset assoc-in [idx task-key] points))

(def evaluate-1-map
  {1 "正しい文"
   -1 "正しくない文"
   0 "判断できない"
   -2 "None"})

(defn evaluate-1-dataset []
  (let [raw_sentences (:raw_sentences @dataset)]
    [:div
     [:section.section>div.container
      [:h2 "評価1 (正しい文分割)"]
      [:p "データが正しく文単位で分割されているかを評価します。"]]
     [:ul
      (doall (for [sent raw_sentences]
               (do
                 (when-not  (get-in @evaluate-dataset [(:id sent) :task1])
                   (add-evaluate (:id sent) :task1 -2))
                 [:li {:key (:id sent)}
                  [:div.box [:div.content (:sentence sent)]
                   [:nav.level.is-mobile
                    [:div.level-left
                     [:a.level-item {:on-click #(add-evaluate (:id sent) :task1 1)} "正しい文"]
                     [:a.level-item  {:on-click #(add-evaluate (:id sent) :task1 -1)} "正しくない文"]
                     [:a.level-item {:on-click #(add-evaluate (:id sent) :task1 0)} "判断できない"]]
                    [:div.level-left
                     [:p.level-item "your evaluation"]
                     [:p.level-item (evaluate-1-map (get-in @evaluate-dataset [(:id sent) :task1]))]]]]])))]
     [:section.section>div.container
      [:div.column [:button.button.is-link
                    {:on-click #(do
                                  (reset! state :evaluate-2-dataset))}
                    "2nd evaluation " [:i.material-icons "arrow_forward"]]]
      [:div.column [:button.button.is-link
                    {:on-click #(do
                                  (reset! state :view-dataset))}
                    "back to view dataset info " [:i.material-icons "first_page"]]]
      [:div.column [:button.button.is-info
                    {:on-click #(do
                                  (reset! state :select-dataset))}
                    "back to selection dataset" [:i.material-icons "cancel"]]]]]))

(def evaluate-2-1-map
  {1 "補完している"
   -1 "補完していない"
   0 "判断できない"
   -2 "None"})

(def evaluate-2-2-map
  {1 "保たれている"
   -1 "保たれていない"
   0 "判断できない"
   -2 "None"})

(defn push-evaluation [evaluates path]
  (let [body {:path (str path) :evaluation (into {}  (for [[k v]  evaluates]  [(keyword (str k)) v]))}]
    (PUT "/api/files/evaluate"
      {:params body
       :format :json})))

(defn evaluate-2-dataset []
  (let [ls-sents (:lack_subject_sentences @dataset)
        cs-sents (:complement_subject_sentences @dataset)
        sent-pairs (map vector ls-sents cs-sents)]
    [:div
     [:section.section>div.container
      [:h2 "評価2(正しい文意・文の自然さ)"]
      [:p "データを尤もらしく補完できているかを評価します。"]]
     [:ul (doall
           (for [[ls-sent cs-sent] sent-pairs]
             (do
               (if-not  (get-in @evaluate-dataset [(:id cs-sent) :task2-1])
                 (do (add-evaluate (:id ls-sent) :task2-1 -2)
                     (add-evaluate (:id ls-sent) :task2-2 -2)))
               [:li ^{:key (first ls-sent)} [:div.box
                                             [:div.content [:p (second ls-sent)]]
                                             [:div.content [:p [:i.material-icons "subdirectory_arrow_right"]  (second cs-sent)]]
                                             [:ol
                                              [:li
                                               ^{:key (str (first ls-sent) "-1")}
                                               [:nav.level.is-mobile
                                                [:div.level-left
                                                 [:p.level-item {:style {:margin-bottom "0"}} "適切な主語を補完している"]
                                                 [:a.level-item {:on-click #(add-evaluate (:id ls-sent) :task2-1 1)} "補完している"]
                                                 [:a.level-item {:on-click #(add-evaluate (:id ls-sent) :task2-1 -1)} "補完していない"]
                                                 [:a.level-item {:on-click #(add-evaluate (:id ls-sent) :task2-1 0)} "判断できない"]]
                                                [:div.level-right
                                                 [:p.level-item "your eval."]
                                                 [:p.level-item (evaluate-2-1-map (get-in @evaluate-dataset [(:id ls-sent) :task2-1]))]]]]
                                              [:li
                                               ^{:key (str (first ls-sent) "-2")}
                                               [:nav.level.is-mobile
                                                [:div.level-left
                                                 [:p.level-item {:style {:margin-bottom "0"}} "文が自然なものとして保たている"]
                                                 [:a.level-item {:on-click #(add-evaluate (:id ls-sent) :task2-2 1)} "保たれている"]
                                                 [:a.level-item {:on-click #(add-evaluate (:id ls-sent) :task2-2 -1)} "保たれていない"]
                                                 [:a.level-item  {:on-click #(add-evaluate (:id ls-sent) :task2-2 0)} "判断できない"]]
                                                [:div.level-right
                                                 [:p.level-item "your eval."]
                                                 [:p.level-item (evaluate-2-2-map (get-in @evaluate-dataset [(:id ls-sent) :task2-2]))]]]]]]])))
      [:section.section>div.container
       [:div.columns
        [:div.column
         [:div.content [:button.button.is-link
                        {:on-click #(do
                                      (reset! state :evaluate-1-dataset))}
                        "1st evaluation " [:i.material-icons "arrow_back"]]]
         [:div.content [:button.button.is-link
                        {:on-click #(do
                                      (reset! state :view-dataset))}
                        "back to view dataset info " [:i.material-icons "first_page"]]]
         [:div.content [:button.button.is-info
                        {:on-click #(do
                                      (reset! state :select-dataset))}
                        "back to selection dataset" [:i.material-icons "cancel"]]]]
        [:div.column
         [:div.content [:button.button.is-success
                        {:on-click #(do
                                      (push-evaluation @evaluate-dataset (:path @dataset))
                                      (swap!  evaluated-datasets conj  (last (clojure.string/split (:path @dataset) #"/")))
                                      (reset! state :select-dataset))}
                        "export" [:i.material-icons "check"]]]]]]]]))

(defn home-page []
  (fetch-datasets!)
  [:section.section>div.container>div.content
   (condp = @state
     :select-dataset [load-section]
     :view-dataset [view-dataset]
     :evaluate-1-dataset [evaluate-1-dataset]
     :evaluate-2-dataset [evaluate-2-dataset]
     [:p "sry. unexpected err" [:div.column [:button.button.is-info
                                             {:on-click #(do
                                                           (reset! state :select-dataset))}
                                             "back to selection dataset" [:i.material-icons "cancel"]]]])])




