(ns beavr.doc
  (:require [beavr.options :as options]
            [beavr.text :as text]
            [clojure.string :as str]
            [quark.collection.map :as map]))

(def neodoc (js/require "neodoc"))

(defn find-descriptions
  [help-text options-keys]
  (->> options-keys
       (keep (fn [option]
               (let [txt (options/with-double-dashes option)
                     line (some-> help-text (text/grep txt) last str/trim)
                     description (->> line (re-find #".*?\s{2,}(.*)") last)]
                 [txt description])))
       (into {})))

(defn neodoc-parse
  [doc]
  (-> neodoc
      (.parse doc)
      (js->clj :keywordize-keys true)))

(defn parse
  [docstring]
  (let [{:keys [layouts descriptions helpText]} (neodoc-parse docstring)
        options (options/all layouts)
        options-keys (->> options keys set)]
    {:layouts layouts
     :options options
     :descriptions (find-descriptions helpText options-keys)}))

