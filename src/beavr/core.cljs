(ns beavr.core
  (:require [cljs.nodejs :as nodejs]
            [beavr.doc :as doc]
            [clojure.string :as str]
            [beavr.fixtures :as fixtures]
            [beavr.prompt :as prompt]
            [beavr.suggestions :as suggestions]
            [beavr.layout :as layout]
            [cljs.pprint :as pprint]
            [beavr.text :as text]
            [beavr.options :as options]
            [quark.debug :as debug]
            [beavr.ansi :as ansi]))

(nodejs/enable-util-print!)

(defn myloop
  [{:keys [layouts options]}]
  (loop [context {}
         field nil
         path []]
    (let [debug {:path path
                 :field field
                 :context context}
          possible-layouts (layout/possible-layouts layouts path)
          suggestions (suggestions/find-suggestions options possible-layouts context field path)
          prompt-str  (some-> field text/first-word)
          header (str/join " " path)
          value (if (-> suggestions first keyword?)
                  (prompt/read prompt-str)
                    (-> (prompt/fzf suggestions prompt-str header)
                                          text/first-word) )
          dashed? (some-> value layout/dashed?)
          dashed-with-arg? (and dashed? (-> options (get (options/without-dashes value)) :argument))
          positional? (some-> value layout/positional-argument?)
          terminate? (or (= "TERMINATE" value)
                         (not (some-> value seq)))]
      (cond
        terminate? debug
        (or positional? (and dashed? dashed-with-arg?)) (recur context value path)
        dashed? (recur (assoc context value nil) nil path)
        field (recur (assoc context field value) nil (conj path value))
        :else (recur context nil (conj path value)) ))))

(defn -main []
  (->> (doc/parse fixtures/docstring)
       myloop
       pprint/pprint))

(defn play
  []
  (-> (str/join "\n"
                [ (str ansi/cyan "cyan" ansi/reset)
                 (str ansi/light-blue "light-blue" ansi/reset)
                 (str ansi/light-cyan "light-cyan" ansi/reset)
                 (str ansi/blue "blue" ansi/reset)])
      println)
  (-main))

(set! *main-cli-fn* -main)
;(set! *main-cli-fn* play)
