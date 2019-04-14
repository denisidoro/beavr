(ns quark.debug
  (:require [cljs.pprint :as pprint]))

(defn tap
  [x]
  (pprint/pprint x)
  x)

