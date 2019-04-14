(ns beavr.text
  (:require [clojure.string :as str]))

(defn grep
  [text substr]
  (->> text
       str/split-lines
       (filter #(str/includes? % substr))))

(defn first-word
  ([text]
    (first-word text #";"))
  ([text ifs]
   (-> (str/split text ifs)
       first)))