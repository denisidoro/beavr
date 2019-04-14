(ns beavr.suggestions
  (:require [beavr.layout :as layout]
            [beavr.options :as options]
            [beavr.text :as text]
            [goog.string :as gstr]
            [goog.string.format]
            [quark.debug :as debug]))

(defn based-on-path
  [path layout]
  (let [path-count (count path)]
    (->> layout
         (drop path-count)
         first
         layout/elem-names)))

(defn raw-suggestions!
  [layouts context field path]
  (case  field
    nil (mapcat (partial based-on-path path) layouts)
    "<x>" [::number]
    "<y>" [::number]
    ["lorem" "ipsum" "dolor"]))

(defn without-filled-options
  [context suggestions]
  (let [keyset (-> context keys set)]
    (filter #(-> % text/first-column keyset not) suggestions)))

(defn with-terminate-action
  [suggestions]
  (concat suggestions ["TERMINATE"]))

(defn find-suggestions!
  [possible-layouts context field path]
  (->> (raw-suggestions! possible-layouts context field path)
       (without-filled-options context)
       set
       with-terminate-action))

(defn with-comments
  [descriptions suggestions]
  (let [comments (map (partial get descriptions) suggestions)
        max-length (->> suggestions (map count) (apply max))
        length (+ max-length 2)
        format-str (str "%-" length "s")
        format #(gstr/format format-str %)]
    (map
     (fn [suggestion comment]
       (str (format suggestion) comment))
     suggestions
     comments)))
