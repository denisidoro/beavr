(ns beavr.suggestions
  (:require [beavr.layout :as layout]
            [beavr.options :as options]
            [quark.collection.map :as map]
            [beavr.text :as text]))

(defn based-on-path
  [path layout]
  (let [path-count (count path)]
    (->> layout
         (drop path-count)
first
         layout/elem-names)))

(defn with-description
  [options x]
  (if (layout/dashed? x)
    (str x ";" (-> options
                      (get (options/without-dashes x))
                      :description))
    x))

(defn raw-suggestions
  [layouts context field path]
  (if field
    ["lorem" "ipsum" "dolor"]
    (mapcat (partial based-on-path path) layouts)))

(defn without-filled-options
  [context suggestions]
  (let [keyset (-> context keys set)]
    (filter #(-> % text/first-word keyset not) suggestions)))

(defn find-suggestions
  [options possible-layouts context field path]
  (as-> (raw-suggestions possible-layouts context field path) it
       (without-filled-options context it)
                           (map (partial with-description options) it)
       (concat it ["TERMINATE"])))
