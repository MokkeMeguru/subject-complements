
;; (defn get-width [size margin]
;;   (- (:width size)
;;      (:right margin)
;;      (:left margin)))

;; (defn get-height [size margin]
;;   (- (:height size)
;;      (:top margin)
;;      (:left margin)))

;; (defn gen-orientations [size]
;;   {:bottom-to-top
;;    {:size [(:width size) (:height size)]
;;     :x (fn [d] (.-x d))
;;     :y (fn [d] (- (:height size) (.-y d)))}})

;; (defn remove-svg []
;;   (-> js/d3
;;       (.selectAll "#treegraph svg")
;;       (.remove)))

;; (defn append-tree-svg [size margin]
;;   (remove-svg)
;;   (-> js/d3
;;       (.select "#treegraph")
;;       (.append "svg")
;;       (.attr "id"  "svg"))
;;   (-> js/d3
;;       (.select "#treegraph #svg")
;;       (.data (-> js/d3 (.entries (clj->js (gen-orientations size)))))
;;       (.attr "width" (:width size))
;;       (.attr "height" (:height size))
;;       (.append "g")
;;       (.attr "transform" (gstring/format "trainslate(%d,%d)" (:left margin) (:top margin)))))

;; (defn gen-treemap [size]
;;   (-> js/d3 .tree (.size (clj->js [(:height size) (:width size)]))))

;; (defn gen-nodes [tree-map tree-data] (-> js/d3 (.hierarchy (clj->js tree-data)) tree-map))

;; (defn gen-links [nodes] (-> nodes .descendants (.slice 1)))

;; (append-tree-svg size margin)
;; (def tree-map (-> js/d3 (.select "#treegraph svg g")))

;; (defn gen-gen-tree-graph [svg tree-data]
;;   (let [svg (-> js/d3 (.select svg))]
;;     (fn [orientation]
;;       (let [o (.-value orientation)
;;             tree-map (gen-treemap (.-size o))
;;             nodes (gen-nodes tree-map tree-data)
;;             links (gen-links nodes)]
;;         (-> js/d3
;;             (.selectAll "#treegraph svg .link")
;;             (.data links)
;;              .enter
;;              (.append "path")
;;             (.attr "class" "link")
;;             (.attr "d"
;;                    (fn [d]
;;                      (str "M" (.-x d) "," (.-y d)
;;                           "C" (.-x d) "," (/ (+ ((.-y o) d) ((.-y o) (.-parent d))) 2)
;;                           " " (-> d .-parent .-x)  "," (/ (+ ((.-y o) d) ((.-y o) (.-parent d))) 2)
;;                           " " (-> d .-parent .-x) "," ((.-y o) (.-parent d)))))
;;             )
;;         (-> js/d3
;;             (.selectAll "#treegraph svg  .node")
;;             (.data (.descendants nodes))
;;             .enter
;;             (.append "g")
;;             (.append "circle")
;;             (.attr "class" "node")
;;             (.attr "r" 4.5)
;;             (.attr "cx" (.-x o))
;;             (.attr "cy" (.-y o))
;;             (.append "text")
;;             (.text (fn [d] (-> d .-data .-word)))
;;             (.attr "x" (.-x o))
;;             (.attr "dx" 5)
;;             (.attr "y" (.-y o)))
;;         ))))


;; (.each (append-tree-svg size margin)  gen-tree-graph)

;; ((.-y (.-value (first (-> js/d3 (.entries (clj->js (gen-orientations size))))))) (clj->js {:y 10}))  
;; (remove-svg)





;; --------------------
;; (-> tree-svg (.each (gen-gen-tree-graph svg tree-data)))
;; (-> tree-svg (.selectAll ".link")  (.data links) .enter (.append "path")  (.attr "class" "link") (.attr "d" ))

;; trash

;; (defn preprocess-treedata [data height width]
;;   (let [root (-> js/d3 (.hierarchy data))
;;         tree (-> js/d3 .tree (.size [height (- width 160)]))]
;;     (tree root)
;;     root))

;; (defn draw-treegraph [svg data]
;;   (let [root (preprocess-treedata data height width)]))

;; (def example-hierarchy-tree-data (-> js/d3 (.hierarchy (clj->js client-app.apps.visualize-snlp/example-tree-data))))

;; (-> client-app.apps.visualize-snlp/example-hierarchy-tree-data
;;     .-children
;;     first
;;     .-data
;;     .-name)


;; (defn append-svg []
;;   (-> js/d3
;;       (.select "#slopegraph")
;;       (.append "svg")
;;       (.attr "height" height)
;;       (.attr "width" width)))

;; (defn remove-svg []
;;   (-> js/d3
;;       (.selectAll "#slopegraph svg")
;;       (.remove)))

;; (def example-data {2005 {:natural-gas 0.2008611514256557
;;                          :coal        0.48970650816857986
;;                          :nuclear     0.19367190804075465
;;                          :renewables  0.02374724819670379}
;;                    2015 {:natural-gas 0.33808321253456974
;;                          :coal        0.3039492492908485
;;                          :nuclear     0.1976276775179704
;;                          :renewables  0.08379872568702211}})

;; (defn format-percent [value]
;;   ((.format js/d3 ".2%") value))

;; (defn format-name [name-str]
;;   (-> name-str
;;       (clojure.string/replace "-" " ")
;;       (clojure.string/capitalize)))

;; (defn attrs [e1 m]
;;   (doseq [[k v] m]
;;     (.attr e1 k v)))

;; (def height-scale
;;   (-> js/d3
;;       (.scaleLinear)
;;       (.domain #js [0 1])
;;       (.range #js [(- height 15) 0])))

;; (defn data-join [percent tag class data]
;;   (let [join (-> percent
;;                  (.selectAll (str tag "." class))
;;                  (.data (into-array data)))
;;         enter (-> join
;;                   (.enter)
;;                   (.append tag)
;;                   (.classed class true))]
;;     (-> join (.exit) (.remove))
;;     (.merge join enter)))

;; (def column-1-start (/ width 4))
;; (def column-space (* 3 (/ width 4)))

;; (defn draw-header [svg years]
;;   (-> svg
;;       (data-join "text" "slopegraph-header" years)
;;       (.text (fn [data _] (str data)))
;;       (attrs {"x" (fn [_ index]
;;                     (+ 50 (* index column-space)))
;;               "y" 15})))

;; (defn draw-line [svg data-col-1 data-col-2]
;;   (-> svg
;;       (data-join "line" "slopegraph-line" data-col-1)
;;       (attrs {"x1" (+ 5 column-1-start)
;;               "x2" (- column-space 5)
;;               "y1" (fn [[_ v]]
;;                      (height-scale v))
;;               "y2" (fn [[k _]]
;;                      (height-scale (get data-col-2 k)))})))

;; (defn draw-column [svg data-col index custom-attrs]
;;   (-> svg
;;       (data-join "text" (str "text.slopegraph-column-" index) data-col)
;;       ;; (.selectAll (str "text.slopegraph-column-" index))
;;       ;; (.data (into-array data-col))
;;       ;; (.enter)
;;       ;; (.append "text")
;;       ;; (.classed (str "slopegraph-column" index) true)
;;       (.text (fn [[k v]] (str (format-name (name k)) " " (format-percent v))))
;;       (attrs (merge custom-attrs
;;                     {"y" (fn [[_ v]] (height-scale v))}))))

;; (defn draw-slopegraph [svg data]
;;   (let [data-2005 (get data 2005)
;;         data-2015 (get data 2015)]
;;     (draw-column svg data-2005 1 {"x" column-1-start})
;;     (draw-column svg data-2015 1 {"x" column-space})
;;     (draw-header svg [2005 2015])
;;     (draw-line svg data-2005 data-2015)))
