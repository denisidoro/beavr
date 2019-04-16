(ns beavr.doc
  (:require [beavr.options :as options]
            [beavr.text :as text]
            [clojure.string :as str]
            [beavr.argument :as arg]
            [beavr.shell :as sh]))

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

(defn parse!
  [cmd]
  (let [cmd-str      (str/replace cmd #" " "-")
        doc-sh       (str sh/beavr-home "/" cmd-str ".sh")
        docstring    (sh/source-and-exec doc-sh {} "beavr::help" [])
        {:keys [layouts descriptions helpText]} (neodoc-parse docstring)
        layouts+     (without-empty-names layouts)
        options      (merge (options/from-descriptions descriptions)
                            (options/from-layout layouts+))
        options-keys (->> options keys set)]
    {:layouts       layouts+
     :options       options
     :docstring     docstring
     :command       cmd
     :kebab-command cmd-str
     :descriptions  (find-descriptions helpText options-keys)}))
