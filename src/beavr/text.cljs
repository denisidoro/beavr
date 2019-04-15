(ns beavr.text
  (:require [clojure.string :as str]))

(defn grep
  [text substr]
  (->> text
       str/split-lines
       (filter #(str/includes? % substr))))

(defn first-column
  [x]
  (->> (str/split x "  ")
       first))

(defn with-leading-space
  [x]
  (str x " "))

(defn quoted
  [x]
  (let [x' (str x)]
    (if (str/includes? x' " ")
      (str "\"" x' "\"")
      x')))

