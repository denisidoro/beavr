(ns beavr.core
  (:require [cljs.nodejs :as nodejs]
            [beavr.doc :as doc]
            [clojure.string :as str]
            [beavr.fixtures :as fixtures]
            [beavr.fzf :as fzf]
            [beavr.suggestions :as suggestions]
            [beavr.layout :as layout]
            [cljs.pprint :as pprint]
            [quark.debug :as debug]))

(nodejs/enable-util-print!)

(defn myloop
  [doc]
  (loop [context {}
         field nil
         path ["ship"]]
    (let [debug {:path path
                 :field field
                 :context context}
          layouts (-> doc :layouts (layout/possible-layouts path))
          suggestions (suggestions/find-suggestions doc layouts context field path)
          prompt-str (or field (str/join " " path))
          value (fzf/prompt suggestions prompt-str)
          value-pending? (and value
                              (or (layout/dashed? value)
                                  (layout/positional? value)))]
      (cond
        (not (some-> value seq)) debug
        value-pending? (recur context value path)
        field (recur (assoc context field value) nil (conj path value))
        :else (recur context nil (conj path value)) ))))

(defn -main []
  (->> (doc/parse fixtures/docstring)
       myloop
       pprint/pprint))

(set! *main-cli-fn* -main)
