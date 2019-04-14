(ns beavr.fzf
  (:require [beavr.shell :as sh]
            [clojure.string :as str]
            [quark.debug :as debug]
            [beavr.options :as options]))

(defn quoted
    [x]
  (str "\"" x "\""))

(defn command-str
  [script props]
  (str/join
    " "
    (into [script]
          (map
            (fn [[k v]]
              (str (-> k name options/with-double-dashes) "=" (quoted v)))
            props))))

(defn prompt
  [coll field header]
  (cond
    (not (some-> coll seq))
    nil

    (= 1 (count coll))
    (first coll)

    :else
    (let [prompt-str (str field " ")
          fzf (command-str "fzf" {:height 10 :prompt prompt-str :header header})
                cmd (debug/tap (str "echo \"" (str/join "\n" coll) "\" | " fzf))
          opts #js {:stdio #js ["inherit" "pipe" "inherit"] :shell true}]
            (sh/sh cmd opts))))
