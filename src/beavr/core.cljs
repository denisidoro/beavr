(ns beavr.core
  (:require [beavr.ansi :as ansi]
            [beavr.doc :as doc]
            [beavr.fixtures :as fixtures]
            [beavr.layout :as layout]
            [beavr.options :as options]
            [beavr.prompt :as prompt]
            [beavr.suggestions :as suggestions]
            [beavr.text :as text]
            [cljs.nodejs :as nodejs]
            [cljs.pprint :as pprint]
            [clojure.string :as str]
            [quark.debug :as debug]))

(nodejs/enable-util-print!)

(defn myloop
  [{:keys [layouts options descriptions]}]
  (loop [context {}
         field nil
         path []]
    (let [debug            {:path path
                            :field field
                            :context context}
          _                (debug/tap debug)
          possible-layouts (layout/possible-layouts layouts path)
          suggestions      (suggestions/find-suggestions! possible-layouts context field path)
          prompt-str       (some-> field text/first-column)
          header           (str/join " " path)
          free-input?      (-> suggestions first keyword? )
          suggestions+     (when-not free-input?
                             (suggestions/with-comments descriptions suggestions))
          value            (if free-input?
                             (prompt/read! prompt-str)
                             (-> (prompt/fzf! suggestions+ prompt-str)
                                 text/first-column))
          dashed?          (some-> value layout/dashed?)
          dashed-with-arg? (and dashed?
                                (-> options (get (options/without-dashes value)) :argument))
          positional?      (some-> value layout/positional-argument?)
          terminate?       (or (= "TERMINATE" value)
                               (not (some-> value seq)))
          wait-value?      (or positional?
                               (and dashed? dashed-with-arg?))]
      (cond
        terminate?  debug
        wait-value? (recur context value path)
        dashed?     (recur (assoc context value nil) nil path)
        field       (recur (assoc context field value) nil (conj path value))
        :else       (recur context nil (conj path value))))))

(defn -main []
  (->> (doc/parse fixtures/docstring)
       myloop
       pprint/pprint))

(defn play
  []
  (-> (doc/parse fixtures/docstring)
      (dissoc :layouts)
      pprint/pprint)
  (-main))

(set! *main-cli-fn* -main)
(set! *main-cli-fn* play)
