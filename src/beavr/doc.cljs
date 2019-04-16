(ns beavr.doc
  (:require [beavr.options :as options]
            [beavr.text :as text]
            [clojure.string :as str]
            [quark.collection.map :as map]
            [beavr.argument :as arg]
            [quark.debug :as debug]))

(def neodoc (js/require "neodoc"))

(defn find-descriptions
  [help-text options-keys]
  (->> options-keys
       (keep (fn [option]
               (let [txt         (arg/with-double-dashes option)
                     line        (some-> help-text (text/grep txt) last str/trim)
                     description (->> line (re-find #".*?\s{2,}(.*)") last)]
                 [txt description])))
       (into {})))

(defn neodoc-parse
  [doc]
  (-> neodoc
      (.parse doc)
      (js->clj :keywordize-keys true)))

(defn without-empty-names
  [layouts]
  (map (fn [x] (map (fn [y] (filter #(-> % :elem :name seq) y)) x)) layouts))

(defn parse
  [docstring]
  (let [{:keys [layouts descriptions helpText]} (neodoc-parse docstring)
        layouts+     (without-empty-names layouts)
        options      (merge (options/from-descriptions descriptions)
                            (options/from-layout layouts+))
        options-keys (->> options keys set)]
    {:layouts      layouts+
     :options      options
     :descriptions (find-descriptions helpText options-keys)}))

