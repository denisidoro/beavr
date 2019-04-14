(ns beavr.doc
  (:require [beavr.options :as options]))

(def neodoc (js/require "neodoc"))

(defn neodoc-parse
  [doc]
    (-> neodoc
        (.parse doc)
        (js->clj :keywordize-keys true)))

(defn parse
  [docstring]
  (let [{:keys [layouts descriptions helpText]} (neodoc-parse docstring)
        raw-options (options/all layouts)]
    {:layouts layouts
     :options (options/enhance helpText raw-options)} ))

