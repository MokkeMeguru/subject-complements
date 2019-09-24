(ns evaluation-app.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[evaluation-app started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[evaluation-app has shut down successfully]=-"))
   :middleware identity})
