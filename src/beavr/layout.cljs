(ns beavr.layout
  (:require [beavr.argument :as arg]
            [clojure.string :as str]
            [quark.collection.seq :as seq]
            [quark.debug :as debug]))

(defn elem-name
  [x]
  (let [name    (get-in x [:elem :name])
        option? (= "Option" (get-in x [:elem :type]))]
    (if option?
      (str "--" name)
      name)))

(defn elem-names
  [{:keys [type] :as x}]
  (case type
    "Elem" [(elem-name x)]
    "Group" (->> x :branches (seq/mmapcat elem-name))
    []))

(defn possible?
  [path layout]
  (loop [[p & other-path :as all-path] path
         [l & other-layout] layout]
    (let [elem-set    (some->> l elem-names (keep identity) set)
          empty-name? (-> elem-set first (= ""))]
      (cond
        empty-name? (recur all-path other-layout)
        (not p) true
        (not l) false
        (elem-set p) (recur other-path other-layout)
        (some->> elem-set seq (some arg/positional?)) (recur other-path other-layout)
        :else false))))

(defn possible-layouts
  [layouts path]
  (->> layouts
       (mapcat #(filter (partial possible? path) %))
       vec))
