(ns client-app.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::sentences
 (fn [db]
   (:sentences db)))

(re-frame/reg-sub
 ::tree-count
 (fn [db]
   (:tree-count db)))

(re-frame/reg-sub
 ::remove-relations
 (fn [db]
   (:remove-relations db)))
