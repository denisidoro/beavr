(ns beavr.suggestions
  (:require [beavr.argument :as arg]
            [beavr.layout :as layout]
            [beavr.shell :as sh]
            [beavr.text :as text]
            [clojure.string :as str]
            [goog.string :as gstr]
            [goog.string.format]
            [quark.collection.map :as map]))

(def ^:const terminate "TERMINATE")

(defn based-on-path
  [path layout]
  (let [path-count (count path)]
    (->> layout
         (drop path-count)
         first
         layout/elem-names)))

(defn raw-suggestions!
  [layouts doc-path context field path]
  (case field
    nil (mapcat (partial based-on-path path) layouts)
    (let [file    doc-path
          env     (map/map-keys arg/raw context)
          cmd     "beavr::suggestion"
          args    [(arg/raw field)]
          results (try
                    (->> (sh/source-and-exec file env cmd args)
                         str/split-lines
                         (filter seq))
                    (catch :default _ []))]
      (if (some-> results seq)
        results
        [::text]))))

(defn without-filled-options
  [context suggestions]
  (let [keyset (-> context keys set)]
    (filter #(-> % text/first-column keyset not) suggestions)))

(defn with-terminate-action
  [suggestions]
  (if (every? arg/dashed? suggestions)
    (concat suggestions [terminate])
    suggestions))

(defn with-options
  [options suggestions]
  (let [option-strs                (->> options keys (map arg/with-double-dashes))
        has-non-dashed-suggestion? (->> suggestions (remove arg/dashed?) seq)]
    (if has-non-dashed-suggestion?
      suggestions
      (concat suggestions option-strs))))

(defn find-suggestions!
  [{:keys [options doc-path]} possible-layouts context field path]
  (->> (raw-suggestions! possible-layouts doc-path context field path)
       (filter (some-fn keyword? seq))
       (with-options options)
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
