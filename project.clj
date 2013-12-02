(defproject gtf2bed "0.0.1-SNAPSHOT"
  :description "Convert GENCODE genes for visualization in Dalliance"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]]
  :jvm-opts ["-Xmx11G"]
  :main genes.gtf2bed
  :aot [genes.gtf2bed])
