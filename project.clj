(defproject gtf2bed "0.0.4-SNAPSHOT"
  :description "Convert GENCODE genes for visualization in Dalliance"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/tools.cli "0.3.1"]]
  :jvm-opts ["-Xmx11G"]
  :main genes.gtf2bed
  :aot [genes.gtf2bed genes.genes2bed])
