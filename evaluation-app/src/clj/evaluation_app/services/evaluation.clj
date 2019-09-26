(ns evaluation-app.services.evaluation
  (:require [clojure.java.io :as io]
            [cheshire.core :refer :all]))

(def prefix "datasets")

(defn get-datasets []
  (let [dir (-> prefix
                (io/resource)
                (io/file))
        files (file-seq dir)]
    (filter #(not (re-matches #".*eval.json" %)) (filter #(re-matches #".*json" %) (map #(.getName %) files)))))

(defn get-dataset [filename]
  (let [file (-> (clojure.string/join "/" [prefix filename])
                 (io/resource))]
    (if (io/as-file file)
      (-> file
          (io/reader)
          (parse-stream true))
      {})))

(def evaluate-prefix ".eval.json")

(defn save-evaluate [filename evaluation]
  (let [file  (clojure.string/join "/"  [(->  prefix (io/resource) (io/file)) (str (.getName (io/file filename)) evaluate-prefix)])]
    (with-open [w (io/writer file)]
      (generate-stream evaluation w))))
