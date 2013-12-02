(ns genes.gtf2bed
  (:use clojure.java.io genes.utils genes.gff)
  (:require [clojure.string :as str])
  (:gen-class))

(defn- gtf2bed [repr raw-exons raw-cds]
  (let [reversed?    (= (:strand repr) "-")
        exons        (sort-by :start raw-exons)
        cds          (if raw-cds (sort-by :start raw-cds))
	      gene-id      (get-in repr '[:attrs "gene_id"])
        gene-name    (get-in repr '[:attrs "gene_name"])]
    [(:seq-name repr)
     (- (:start (first exons)) 1)
     (:end (last exons))
     (get-in repr '[:attrs "transcript_id"])
     1000                          ; score
     (:strand repr)
     (if cds
      (- (:start (first cds)) 1) 0)
     (if cds
      (:end (last cds)) 0)
     "0,0,0"
     (count exons)
     (str/join "," (for [e exons] (- (:end e) (:start e) -1)))
     (str/join "," (for [e exons] (- (:start e) (:start (first exons)))))
     gene-id
     gene-name
     (get-in repr '[:attrs "transcript_type"])]))

(defn gtf2beds [gff]
(->>
  (let [{genes "gene" transcripts "transcript" exons "exon" cds "CDS"} (group-by :type gff)
        exons-by-id (group-by #(get-in % '[:attrs "transcript_id"]) exons)
        cds-by-id (group-by #(get-in % '[:attrs "transcript_id"]) cds)]
    (for [t transcripts :let [tid (get-in t '[:attrs "transcript_id"])]]
      (gtf2bed t (exons-by-id tid) (cds-by-id tid))))
  (sort-by second)
  (sort-by first)))

(defn -main [& args]
  (unless (= (count args) 1)
    (fail "Usage: gtf2bed input.gff"))
  (doseq [b (->> (first args)
		 (reader)
		 (parse-gff2)
		 (gtf2beds))]
    (println (str/join "\t" b))))