(ns beavr.core
  (:require [cljs.nodejs :as nodejs]
            [beavr.doc :as doc]
            [clojure.pprint :as pprint]
            [clojure.walk :as walk]
            [clojure.string :as str]
            [quark.collection.seq :as seq]
            [quark.collection.map :as map]))

(nodejs/enable-util-print!)

(defn tap
  [x]
  (pprint/pprint x)
  x)

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

(defn options
  [layouts]
  (let [opts (atom [])]
    (walk/prewalk
      (fn [layout]
        (when (some-> layout :type (= "Option"))
          (swap! opts conj layout))
        layout)
      layouts)
    (->> @opts
         set
         (map (fn [{:keys [name] :as o}] [name o]))
         (into {}))))

(defn prompt
  [coll field]
  (cond
    (not (some-> coll seq)) nil
    (= 1 (count coll)) (first coll)
    :else (let [prompt-str (str "\"" field " \"")
                fzf (str "fzf --height=10 --prompt=" prompt-str)
                cmd (str "echo \"" (str/join "\n" coll) "\" | " fzf)
          opts #js {:stdio #js ["inherit" "pipe" "inherit"] :shell true}]
      (sh cmd opts))))

(def docstring
"Naval Fate.

Usage:
  naval_fate ship new <name>...
  naval_fate ship <name> move <x> <y> [--speed=<kn>]
  naval_fate ship shoot <x> <y>
  naval_fate ship (set|remove) <x> <y> [--moored|--drifting]
  naval_fate mine (set|remove) <x> <y> [--moored|--drifting]
  naval_fate -h | --help
  naval_fate --version

Options:
  -h --help     Show this screen.
  --version     Show version.
  --speed=<kn>  Speed in knots [default: 10].
  --moored      Moored (anchored) mine.
  --drifting    Drifting mine.")

(defn grep
  [text substr]
  (->> text
       str/split-lines
       (filter #(str/includes? % substr))))

(defn enhance-options
  [help-text options]
  (map/map-vals
    (fn [{:keys [name] :as option}]
      (let [txt (str "--" name)
            description (some-> help-text (grep txt) last str/trim)]
        (assoc option :description description)))
    options))

(defn parse
  []
  (let [{:keys [layouts descriptions helpText]} (doc/parse docstring)
        raw-options (options layouts)]
    {:layouts layouts
     :options (enhance-options helpText raw-options)} ))

(defn elem-name
    [x]
  (let [name (get-in x [:elem :name])
        option? (= "Option" (get-in x [:elem :type]))]
        (if option?
          (str "--" name)
          name)) )

(defn elem-names
  [{:keys [type] :as x}]
  (case type
    "Elem" [(elem-name x)]
    "Group" (->> x :branches (seq/mmapcat elem-name))
    []))

(defn positional?
  [x]
  (str/starts-with? x "<"))

(defn dashed?
  [x]
  (str/starts-with? x "-"))

(defn possible?
  [path branch]
  (loop [[p & other-path] path
         [b & other-branch] branch]
    (let [elem-set (some->> b elem-names (keep identity) set)]
    (cond
      (not p) true
      (not b) false
      (elem-set p) (recur other-path other-branch)
      (some->> elem-set seq (some positional?)) (recur other-path other-branch)
      :else false))))

(defn suggestions
  [path branch]
  (let [path-count (count path)]
    (->> branch
         (drop path-count)
         first
         elem-names)))

(defn find-suggestions
  [paths context field path]
  (if field
    ["lorem" "ipsum" "dolor"]
    (mapcat (partial suggestions path) paths)))

(defn possible-paths
  [description path]
    (->> description
         :layouts
         (mapcat #(filter (partial possible? path) %))
         vec))

(defn with-description
  [spec x]
  (if (dashed? x)
    (str x "    " (-> spec :options :description))
    x))

(defn myloop
  [spec]
  (loop [context {}
         field nil
         path ["ship"]]
    (let [debug {:path path
                 :field field
                 :context context}
          paths (possible-paths spec path)
          suggestions (->> (find-suggestions paths context field path)
                           (map (partial with-description spec)))
          prompt-str (or field (str/join " " path))
          value (prompt suggestions prompt-str)
          value-pending? (and value
                              (or (dashed? value)
                                  (positional? value)))]
      (cond
        (not (some-> value seq)) debug
        value-pending? (recur context value path)
        field (recur (assoc context field value) nil (conj path value))
        :else (recur context nil (conj path value)) ))))

(defn -main []
  (->> (parse)
       myloop
       pprint/pprint))

(defn -main1 []
  (->> (parse)
       pprint/pprint))

(set! *main-cli-fn* -main)
