(ns client-app.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [client-app.events :as events]
   [client-app.views :as views]
   [client-app.config :as config]
   [client-app.apps.visualize-snlp :as vs]
   ))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch [::events/initialize-db])
  (re-frame/dispatch [::events/init-tree-count])
  (re-frame/dispatch [::events/initialize-sentences])
  (dev-setup)
  (mount-root)
  (vs/init-sentence-tree 0))
