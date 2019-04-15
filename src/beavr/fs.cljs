(ns beavr.fs)

(def ^:private fs (js/require "fs"))

(defn slurp
  [file]
  (.readFileSync fs file "utf8"))

