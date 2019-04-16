(ns beavr.shell
  (:require [beavr.text :as text]
            [clojure.string :as str]
            [quark.debug :as debug]))

(def ^:private proc (js/require "child_process"))
(def process (js/require "process"))
(def stdout (.-stdout process))
(def stdin (.-stdin process))
(def env (.-env process))

(defn env-var
  [name]
  (aget env name))

(def home
  (env-var "HOME"))

(def beavr-home
  (str home "/.config/beavr"))

(defn sh
  ([cmds]
   (sh cmds #js {:cwd "/Users/denis" :env env :terminal true}))
  ([cmds opts]
   (let [[cmd & args] (if (string? cmds) [cmds] cmds)]
     (-> proc
         (.spawnSync cmd (clj->js (or args [])) opts)
         js->clj
         (get "stdout")
         str
         str/trim))))

(defn source-and-exec
  [file env cmd args]
  (let [export-str (reduce (fn [s [k v]] (str s " export " k "=" v "; ")) "" env)
        args-str   (->> args (map text/quoted) (str/join " "))
        bash-str   (str export-str " source " file "; " cmd " " args-str ";")]
    (sh ["bash" "-c" bash-str])))
