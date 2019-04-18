(ns beavr.core
  (:require [beavr.argument :as arg]
            [beavr.cmd :as cmd]
            [beavr.doc :as doc]
            [beavr.layout :as layout]
            [beavr.prompt :as prompt]
            [beavr.suggestions :as suggestions]
            [beavr.text :as text]
            [cljs.nodejs :as nodejs]
            [clojure.string :as str]))

(nodejs/enable-util-print!)

(defn input-loop
  [inputs
   {:keys [layouts options descriptions] :as doc}]
  (loop [context {}
         field   nil
         path    []
         [first-input & remaining-inputs :as all-inputs] inputs]
    (let [possible-layouts (layout/possible-layouts layouts path)
          suggestions      (suggestions/find-suggestions! doc possible-layouts context field path)
          prompt-str       (some-> field text/first-column)
          free-input?      (-> suggestions first keyword?)
          suggestions+     (when-not free-input?
                             (suggestions/with-comments descriptions suggestions))
          require-input?   (not (prompt/skip-input? suggestions+))
          input            (when require-input? first-input)
          next-inputs      (if input remaining-inputs all-inputs)
          fzf-props        (if input {:filter input} {})
          value            (if (or input (not free-input?))
                             (-> (prompt/select! suggestions+ prompt-str fzf-props)
                                 text/first-column)
                             (prompt/read! prompt-str))
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
        dashed-field? (recur (assoc context field value) nil path next-inputs)
        wait-value? (recur context value path next-inputs)
        dashed-value? (recur (assoc context value nil) nil path next-inputs)
        field (recur (assoc context field value) nil (conj path value) next-inputs)
        :else (recur context nil (conj path value) next-inputs)))))

(defn -main
  [& args]
  (->> (str/join " " args)
       doc/parse!
       (input-loop [])
       println))

(set! *main-cli-fn* -main)
