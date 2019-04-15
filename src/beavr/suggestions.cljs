(ns beavr.suggestions
  (:require [beavr.layout :as layout]
            [beavr.text :as text]
            [goog.string :as gstr]
            [goog.string.format]
            [quark.debug :as debug]
            [beavr.shell :as sh]
            [clojure.string :as str]
            [beavr.argument :as arg]
            [quark.collection.map :as map]))

(def ^:private ^:const terminate "TERMINATE")

(defn based-on-path
  [path layout]
  (let [path-count (count path)]
    (->> layout
         (drop path-count)
         first
         layout/elem-names)))



(defn raw-suggestions!
  [layouts context field path]
  (case field
    nil (mapcat (partial based-on-path path) layouts)
    "<x>" [::number]
    "<y>" [::number]
    (-> (sh/source-and-exec "/Users/denis/.config/beavr/nu-ser-curl.sh" (map/map-keys arg/raw context) (str "suggestion::" (arg/raw field)))
        str/split-lines)))

(defn without-filled-options
  [context suggestions]
  (let [keyset (-> context keys set)]
    (filter #(-> % text/first-column keyset not) suggestions)))

(defn with-terminate-action
  [suggestions]
  (if (every? arg/dashed? suggestions)
    (concat suggestions [terminate])
    suggestions))

(defn find-suggestions!
  [possible-layouts context field path]
  (->> (raw-suggestions! possible-layouts context field path)
       (without-filled-options context)
       set
       with-terminate-action))

(defn with-comments
  [descriptions suggestions]
  (let [comments   (map (partial get descriptions) suggestions)
        max-length (->> suggestions (map count) (apply max))
        length     (+ max-length 6)
        format-str (str "%-" length "s")
        format     #(gstr/format format-str %)]
    (map
      (fn [suggestion comment]
        (str (format suggestion) comment))
      suggestions
      comments)))
