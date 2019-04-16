(ns beavr.core
  (:require [beavr.cmd :as cmd]
            [beavr.doc :as doc]
            [beavr.fixtures :as fixtures]
            [beavr.layout :as layout]
            [beavr.options :as options]
            [beavr.prompt :as prompt]
            [beavr.suggestions :as suggestions]
            [beavr.fs :as fs]
            [beavr.text :as text]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]
            [beavr.shell :as sh]
            [quark.debug :as debug]
            [beavr.argument :as arg]
            [cljs.pprint :as pprint]))

(nodejs/enable-util-print!)

(defn myloop
  [{:keys [layouts options descriptions]}]
  (loop [context {}
         field   nil
         path    []]
    (let [possible-layouts (layout/possible-layouts layouts path)
          suggestions      (suggestions/find-suggestions! possible-layouts options context field path)
          prompt-str       (some-> field text/first-column)
          free-input?      (-> suggestions first keyword?)
          suggestions+     (when-not free-input?
                             (suggestions/with-comments descriptions suggestions))
          value            (if free-input?
                             (prompt/read! prompt-str)
                             (-> (prompt/fzf! suggestions+ prompt-str)
                                 text/first-column))
          dashed-value?    (some-> value arg/dashed?)
          dashed-field?    (some-> field arg/dashed?)
          dashed-with-arg? (and dashed-value?
                                (-> options (get (arg/without-dashes value)) :argument))
          positional?      (some-> value arg/positional?)
          terminate?       (or (= suggestions/terminate value)
                               (not (some-> value seq)))
          wait-value?      (or positional?
                               (and dashed-value? dashed-with-arg?))]
      (cond
        terminate? (cmd/build-final-cmd context path)
        dashed-field? (recur (assoc context field value) nil path)
        wait-value? (recur context value path)
        dashed-value? (recur (assoc context value nil) nil path)
        field (recur (assoc context field value) nil (conj path value))
        :else (recur context nil (conj path value))))))

(defn parse
  [cmd]
  (-> (str (sh/env-var "HOME")
           "/.config/beavr/"
           cmd
           ".sh")
      fs/slurp
      (str/replace "##? " "")))

(defn -main []
  (->> "nu-ser-curl"
       parse
       doc/parse
       myloop
       pprint/pprint
       ;println
       ))

(set! *main-cli-fn* -main)
