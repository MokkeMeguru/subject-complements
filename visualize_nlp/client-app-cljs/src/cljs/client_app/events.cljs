(ns client-app.events
  (:require
   [re-frame.core :as re-frame]
   [ajax.core :as ajax]
   [client-app.db :as db]
   [day8.re-frame.http-fx] 
   [client-app.subs :as subs]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::reset-sentences
 (fn [db sentences]
   (assoc db :sentences sentences)))

(re-frame/reg-event-db
 ::initialize-sentences
 (fn [db _]
   (assoc db :sentences
          {"result_tree"
           [[{"children"
              [{"children"
                [{"children"
                  [{"children" [],
                    "dependency_relation" "case",
                    "governor" 1,
                    "index" 2,
                    "lemma" "の",
                    "raw_word" "の",
                    "upos" "ADP"}],
                  "dependency_relation" "nmod",
                  "governor" 3,
                  "index" 1,
                  "lemma" "あなた",
                  "raw_word" "あなた",
                  "upos" "PRON"}
                 {"children" [],
                  "dependency_relation" "case",
                  "governor" 3,
                  "index" 4,
                  "lemma" "は",
                  "raw_word" "は",
                  "upos" "ADP"}],
                "dependency_relation" "nsubj",
                "governor" 5,
                "index" 3,
                "lemma" "進捗",
                "raw_word" "進捗",
                "upos" "NOUN"}
               {"children" [],
                "dependency_relation" "cop",
                "governor" 5,
                "index" 6,
                "lemma" "だ",
                "raw_word" "です",
                "upos" "AUX"}
               {"children" [],
                "dependency_relation" "mark",
                "governor" 5,
                "index" 7,
                "lemma" "か",
                "raw_word" "か",
                "upos" "PART"}
               {"children" [],
                "dependency_relation" "punct",
                "governor" 5,
                "index" 8,
                "lemma" "？",
                "raw_word" "？",
                "upos" "PUNCT"}],
              "dependency_relation" "root",
              "governor" 0,
              "index" 5,
              "lemma" "どう",
              "raw_word" "どう",
              "upos" "ADV"}]]})))

(re-frame/reg-event-db
 ::init-tree-count
 (fn [db _]
   (assoc db :tree-count 0)))

(re-frame/reg-event-db
 ::init-remove-relations
 (fn [db _]
   (assoc db :remove-relations #{})))

(re-frame/reg-event-db
 ::add-remove-relation
 (fn [db [k v]]
   (update db  :remove-relations conj v)))

(re-frame/reg-event-db
 ::sub-remove-relation
 (fn [db [k v]]
   (update db  :remove-relations disj v)))


(re-frame/reg-event-db
 ::inc-tree-count
 (fn [db _]
   (assoc db :tree-count (inc @(re-frame/subscribe [::subs/tree-count])))))


(re-frame/reg-event-db
 ::success-analyze-text
 (fn [db [_ res]]
   (print res)
   (.log js/console res)
   db))

(re-frame/reg-event-db
 ::failure-analyze-text
 (fn [db [_ res]]
   (print res)
   (.log js/console res)
   db))

(re-frame/reg-event-fx
 ::send-text
 (fn [db [_ params]]
   {:http-xhrio
    {:method :post
     :uri "http://localhost:8810/parse-snlp/dependencies"
     :params {:text params :app "parse-snlp"}
     :timeout 8000
     :format (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})
     :on-success [::success-analyze-text]
     :on-failure [::failure-analyze-text]
     }}
   ))

;; (re-frame/dispatch [::send-text "怖い話ではありません。"])


