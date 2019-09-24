(ns evaluation-app.app
  (:require [evaluation-app.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
