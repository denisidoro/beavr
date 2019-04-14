(ns beavr.options
  (:require [clojure.walk :as walk]
            [clojure.string :as str]
            [beavr.text :as text]
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

(defn enhance
  [help-text options]
  (map/map-vals
    (fn [{:keys [name] :as option}]
      (let [txt (with-double-dashes name)
            description (some-> help-text (text/grep txt) last str/trim)]
        (assoc option :description description)))
    options))

