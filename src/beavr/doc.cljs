(ns beavr.doc)

(def neodoc (js/require "neodoc"))

(defn parse
  [doc]
    (-> neodoc
        (.parse doc)
        (js->clj :keywordize-keys true)))
