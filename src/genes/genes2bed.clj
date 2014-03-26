(ns genes.genes2bed
  (:use clojure.java.io genes.utils genes.gff)
  (:require [clojure.string :as str])
  (:gen-class))

(defn- genebed [mrna gene gf]
  (let [features      (map-by :type (sort-by :start gf))
        exons         (features "exon")
        cds           (features "CDS")
        utr3          (features "three_prime_UTR")
        utr5          (features "five_prime_UTR")
      	repr          (first exons)
      	reversed?     (= (:strand repr) "-")
      	gene-id       (first (get-in gene [:attrs "ID"]))
        gene-name     (first (get-in gene [:attrs "Name"]))
        [cds-min cds-max]
          (if-not (nil? cds)
            [(:start (first cds)) 
             (:end   (last cds))]
            [(or (:end (last (if reversed? utr3 utr5)))
                 (:start (first exons)))
             (or (:start (first (if reversed? utr5 utr3)))
                 (:end (last exons)))])]
    [(:seq-name repr)
     (dec (:start (first exons)))
     (:end (last exons))
     (first (get-in repr '[:attrs "Parent"]))
     1000                          ; score
     (:strand repr)
     (dec cds-min)
     cds-max
     "0,0,0"
     (count exons)
     (str/join "," (for [e exons] (- (:end e) (:start e) -1)))
     (str/join "," (for [e exons] (- (:start e) (:start (first exons)))))
     gene-id
     (or gene-name gene-id)
     (:source mrna)
     "-"]))


(defn genebeds [gff]
  (->> (let [by-parent (map-by-attrib gff "Parent")
	     by-id (map-by-attrib gff "ID")]
	 (for [m gff :when (= (:type m) "mRNA")]
	   (let [id     (first (get-in m '[:attrs "ID"]))
		 parent (first (get-in m '[:attrs "Parent"]))]
	     (genebed m (first (get by-id parent)) (get by-parent id)))))
       (sort-by second)
       (sort-by first)))

(defn -main [& args]
  (unless (= (count args) 1)
    (fail "Usage: genes2bed input.gff"))
  (doseq [b (->> (first args)
		 (reader)
		 (parse-gff)
		 (genebeds))]
    (println (str/join "\t" b))))
