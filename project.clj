(defproject beavr "0.1.3-SNAPSHOT"
  :description "A command-line autocompleter with steroids"
  :url "https://github.com/denisidoro/beavr"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.520"]
                 [org.clojure/core.rrb-vector "0.0.14"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-figwheel "0.5.13"]
            [lein-cljfmt "0.5.7"]
            [lein-nsorg "0.2.0"]
            [lein-doo "0.1.11"]]

  :source-paths ["src"]

  :clean-targets ["server.js"
                  "target"]

  :cljsbuild
  {:builds [{:id           "dev"
             :source-paths ["src"]
             :figwheel     true
             :compiler     {:main                 beavr.core
                            :asset-path           "target/js/compiled/dev"
                            :output-to            "target/js/compiled/beavr.js"
                            :output-dir           "target/js/compiled/dev"
                            :target               :nodejs
                            :optimizations        :none
                            :source-map-timestamp true}}
            {:id           "prod"
             :source-paths ["src"]
             :compiler     {:output-to     "bin/index.js"
                            :output-dir    "target/js/compiled/prod"
                            :target        :nodejs
                            :optimizations :simple}}
            {:id           "test"
             :source-paths ["src" "test"]
             :compiler     {:output-to     "target/testable.js"
                            :output-dir    "target"
                            :main          beavr.runner
                            :target        :nodejs
                            :optimizations :none}}]}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :cljfmt {:indents {flow [[:block 2]]}})
