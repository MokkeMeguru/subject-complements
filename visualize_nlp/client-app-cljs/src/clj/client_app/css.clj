(ns client-app.css
  (:require [garden.def :refer [defstyles]]))

(defstyles screen
  [:body {:color "black"}]
  [:.link {:fill "none"
           :stroke "#ccc"
           :stroke-opacity 0.9
           :stroke-width "3px"}]
  [:.node [:circle
           {:fill "#fff"
            :stroke "steelblue"
            :stroke-width "1.5px"}]]
  [:.tooltip
   {:position "absolute"
    :text-align "center"
    :width "auto"
    :height "auto"
    :padding "5px"
    :font "12px"
    :background "white"
    :-webkit-box-shadow "0px 0px 20px rgba(0, 0, 0, 0.8)"
    :-moz-box-shadow "0px 0px 20px rgba(0, 0, 0, 0.8)"
    :box-shadow "0px 0px 20px rgba(0, 0, 0, 0.8)"
    :visibility "visible"
    :opacity "0.9"}]
  [:.bar:hover
   {:fill "Brown"}])
