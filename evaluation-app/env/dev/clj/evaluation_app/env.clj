(ns evaluation-app.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [evaluation-app.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[evaluation-app started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[evaluation-app has shut down successfully]=-"))
   :middleware wrap-dev})
