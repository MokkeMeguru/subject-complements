(ns evaluation-app.services.evaluation
  (:require [clojure.java.io :as io]))

(defn get-datasets []
  (let [dir (-> "datasets"
                (io/resource)
                (io/file))
        files (file-seq dir)]
    (filter #(re-matches #".*json" %) (map #(.getName %) files))))
