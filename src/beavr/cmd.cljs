(ns beavr.cmd
  (:require [beavr.layout :as layout]
            [beavr.text :as text]
            [clojure.string :as str]
            [quark.collection.map :as map]
            [beavr.argument :as arg]))

(defn ^:private context-kv->text
  [[k v]]
  (if v
    (str k " " (text/quoted v))
    (str k)))

(defn build-final-cmd
  [context path]
  (let [path-elems    (map text/quoted path)
        context-elems (->> context (map/filter-keys arg/dashed?) (map context-kv->text))]
    (str/join " " (concat path-elems context-elems))))
