(ns client-app.views
  (:require
   [re-frame.core :as re-frame]
   [client-app.subs :as subs]
   ))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div.container
     [:nav.navbar.navbar-expand.flex-column.flex-md-row.bd-navbar [:h1.text-primary "Hello from " @name]]
     [:div.container {:style {:width "650px"}}
      [:div.row
       [:div.mx-auto {:style {:width "200px"}} "テキスト入力欄"]
       [:textarea.form-control {:rows 3 :placeholder "テキスト"} ]]]
     [:div#treegraph]
     [:div#animation]
     [:div#bar-chart]
     ]))
