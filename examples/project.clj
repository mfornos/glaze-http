(defproject com.github.mfornos/glaze-examples "0.0.1-SNAPSHOT"
  :description "A project for doing things."
  :source-paths [ "src/main/clojure" ]
  ;; lein v1 compatibility
  :source-path "src/main/clojure"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.github.mfornos/glaze-core "0.0.1-SNAPSHOT"]]
  :main glaze.examples.simple)