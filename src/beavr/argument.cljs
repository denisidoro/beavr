(ns beavr.argument
  (:require [clojure.string :as str]
            [quark.collection.seq :as seq]))

(defn positional?
  [x]
  (and (string? x)
       (str/starts-with? x "<")))

(defn dashed?
  [x]
  (and (string? x)
       (str/starts-with? x "-")))

(defn with-double-dashes
  [x]
  (str "--" x))

(defn without-dashes
  [x]
  (str/replace x #"\-" ""))

(defn raw
  [s]
  (str/replace s #"[\<\>]" ""))
