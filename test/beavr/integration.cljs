(ns beavr.integration
  (:require [cljs.test :as t]
            [beavr.doc :as doc]))

(def docstring
"kubectl controls the Kubernetes cluster manager

Usage:
 kubectl get <resource>
 kubectl describe <resource> <id>")

(t/deftest neodoc
  (t/is (= {:program      "kubectl"
            :layouts      [[[{:type "Elem"
                              :elem {:type       "Command"
                                     :name       "get"
                                     :repeatable false}}
                             {:type "Elem"
                              :elem {:type       "Positional"
                                     :name       "<resource>"
                                     :repeatable false}}]]
                           [[{:type "Elem"
                              :elem {:type       "Command"
                                     :name       "describe"
                                     :repeatable false}}
                             {:type "Elem"
                              :elem {:type       "Positional"
                                     :name       "<resource>"
                                     :repeatable false}}
                             {:type "Elem"
                              :elem {:type       "Positional"
                                     :name       "<id>"
                                     :repeatable false}}]]]
            :descriptions []
            :helpText     "kubectl controls the Kubernetes cluster manager\n\nUsage:\n kubectl get <resource>\n kubectl describe <resource> <id>"
            :shortHelp    "Usage:\n kubectl get <resource>\n kubectl describe <resource> <id>"}
           (doc/neodoc-parse docstring))))
