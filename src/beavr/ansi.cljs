(ns beavr.ansi
  (:require [clojure.string :as str]))

(def ^:private ^:const csi
  "The control sequence initiator: `ESC [`"
  "\u001b[")

(def ^:private ^:const sgr
  "The Select Graphic Rendition suffix: m"
  "m")

(def reset (str csi "0m"))
(def default (str csi "39m"))
(def black (str csi "30m"))
(def red (str csi "31m"))
(def green (str csi "32m"))
(def yellow (str csi "33m"))
(def blue (str csi "34m"))
(def magenta (str csi "35m"))
(def cyan (str csi "36m"))
(def light-blue (str csi "94m"))
(def light-cyan (str csi "96m"))
