(ns beavr.cmd
  (:require [beavr.argument :as arg]
            [beavr.text :as text]
            [clojure.string :as str]
            [quark.collection.map :as map]))

(defn ^:private context-kv->text
  [[k v]]
  (if v
    (str k " " (text/quoted v))
    (str k)))

(defn build-final-cmd
  [{:keys [command]} context path]
  (let [path-elems    (map text/quoted path)
        context-elems (->> context (map/filter-keys arg/dashed?) (map context-kv->text))]
    (str/join " " (concat [command] path-elems context-elems))))
