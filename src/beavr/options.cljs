(ns beavr.options
  (:require [beavr.text :as text]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [quark.collection.map :as map]
            [beavr.argument :as arg]))

(defn from-layout
  [layouts]
  (let [opts (atom [])]
    (walk/prewalk
      (fn [layout]
        (when (some-> layout :type (= "Option"))
          (swap! opts conj layout))
        layout)
      layouts)
    (->> @opts
         set
         (map (fn [{:keys [name] :as o}] [name o]))
         (into {}))))

(defn from-descriptions
  [descriptions]
  (->> descriptions
       (map (fn [description] [(-> description :aliases last arg/without-dashes) description]))
       (into {})))
