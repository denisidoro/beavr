(ns beavr.core
  (:require [beavr.cmd :as cmd]
            [beavr.doc :as doc]
            [beavr.layout :as layout]
            [beavr.prompt :as prompt]
            [beavr.suggestions :as suggestions]
            [beavr.text :as text]
            [cljs.nodejs :as nodejs]
            [beavr.argument :as arg]
            [clojure.string :as str]))

(nodejs/enable-util-print!)

(defn input-loop
  [{:keys [layouts options descriptions] :as doc}]
  (loop [context {}
         field   nil
         path    []]
    (let [possible-layouts (layout/possible-layouts layouts path)
          suggestions      (suggestions/find-suggestions! doc possible-layouts context field path)
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
        terminate? (cmd/build-final-cmd doc context path)
        dashed-field? (recur (assoc context field value) nil path)
        wait-value? (recur context value path)
        dashed-value? (recur (assoc context value nil) nil path)
        field (recur (assoc context field value) nil (conj path value))
        :else (recur context nil (conj path value))))))

(defn -main
  [& args]
  (->> (str/join " " args)
       doc/parse!
       input-loop
       println))

(set! *main-cli-fn* -main)
