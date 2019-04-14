(ns beavr.shell
  (:require [clojure.string :as str] ))

(def ^:private proc (js/require "child_process"))

(defn sh
  ([cmd]
    (sh cmd #js {}))
  ([cmd opts]
   (-> proc
       (.spawnSync cmd opts)
       js->clj
       (get "stdout")
       str
       str/trim)))

