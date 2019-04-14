(defproject beavr "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.908"]]

  :plugins [[lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-figwheel "0.5.13"]
            [lein-cljfmt "0.5.7"]
            [lein-nsorg "0.2.0"]]

  :source-paths ["src"]

  :clean-targets ["server.js"
                  "target"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :figwheel true
              :compiler {
                :main beavr.core
                :asset-path "target/js/compiled/dev"
                :output-to "target/js/compiled/beavr.js"
                :output-dir "target/js/compiled/dev"
                :target :nodejs
                :optimizations :none
                :source-map-timestamp true}}
             {:id "prod"
              :source-paths ["src"]
              :compiler {
                :output-to "server.js"
                :output-dir "target/js/compiled/prod"
                :target :nodejs
                :optimizations :simple}}]}

  :profiles {:dev {:dependencies [[figwheel-sidecar "0.5.13"]
                                  [com.cemerick/piggieback "0.2.2"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :cljfmt {:indents {flow               [[:block 1]]
                     facts              [[:block 1]]
                     idempotent-start   [[:block 1]]
                     idempotent-stop    [[:block 1]]
                     fact               [[:block 1]]
                     non-test-action    [[:inner 0]]
                     as-customer        [[:block 1]]
                     as-of              [[:block 1]]
                     assoc-if           [[:block 1]]
                     let-entities       [[:block 2]]
                     provided           [[:inner 0]]
                     tabular            [[:inner 0]]
                     try-type           [[:block 0]]
                     with-fn-validation [[:block 0]]
                     embeds             [[:block 0]]
                     with-responses     [[:block 0]]
                     defint             [[:block 0]]
                     with-scopes        [[:block 1]]
                     some-with-open     [[:block 1]]
                     let-flow           [[:block 1]]
                     defhandler         [[:block 2]]}} )
