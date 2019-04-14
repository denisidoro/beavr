(ns beavr.layout
  (:require [clojure.string :as str]
            [quark.collection.seq :as seq]))

(defn elem-name
  [x]
  (let [name (get-in x [:elem :name])
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

(defn positional-argument?
  [x]
  (str/starts-with? x "<"))

(defn dashed?
  [x]
  (str/starts-with? x "-"))

(defn possible?
  [path layout]
  (loop [[p & other-path] path
         [l & other-layout] layout]
    (let [elem-set (some->> l elem-names (keep identity) set)]
      (cond
        (not p) true
        (not l) false
        (elem-set p) (recur other-path other-layout)
        (some->> elem-set seq (some positional-argument?)) (recur other-path other-layout)
        :else false))))

(defn possible-layouts
  [layouts path]
  (->> layouts
       (mapcat #(filter (partial possible? path) %))
       vec))
