(ns beavr.options
  (:require [beavr.text :as text]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [quark.collection.map :as map]))

(defn all
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

(defn with-double-dashes
  [x]
  (str "--" x))

(defn without-dashes
  [x]
  (str/replace x #"\-" ""))


