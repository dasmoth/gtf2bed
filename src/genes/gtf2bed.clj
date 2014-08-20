(ns genes.gtf2bed
  (:use clojure.java.io genes.utils genes.gff)
  (:require [clojure.string :as str])
  (:gen-class))

(defn- gtf2bed
  "Convert GTF data into 'GENCODE'-style bed (bed12 with 5 extra columns)"
  [repr raw-exons raw-cds]
  (let [reversed?    (= (:strand repr) "-")
        exons        (sort-by :start raw-exons)
        cds          (if raw-cds (sort-by :start raw-cds))
        trans-id     (get-in repr '[:attrs "transcript_id"])
        gene-id      (get-in repr '[:attrs "gene_id"])
        gene-name    (get-in repr '[:attrs "gene_name"])
        tags         (get-in repr '[:attrs "tag"])
        type         (get-in repr '[:attrs "transcript_type"])
        gene-type    (get-in repr '[:attrs "gene_type"])]
    [(:seq-name repr)
     (- (:start (first exons)) 1)
     (:end (last exons))
     trans-id
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
     (or gene-id trans-id)
     (or gene-name gene-id trans-id)
     (or type "-")
     (or gene-type "-")
     (or tags "-")]))

(defn- exon-phase [e cds]
  (let [f (:frame 
           (first 
            (filter 
             (fn [c]
               (and (>= (:start c) (:start e))
                    (<= (:end c) (:end e))))
             cds)))]
    (cond
     (= f 0)   0
     (= f 1)   2
     (= f 2)   1
     :default -1)))
                               
(defn- gtf2bed20 
  "Convert GTF into the bed20/bigGenePred format"
  [repr raw-exons raw-cds]
  (let [reversed?    (= (:strand repr) "-")
        exons        (sort-by :start raw-exons)
        cds          (if raw-cds (sort-by :start raw-cds))
        trans-id     (get-in repr '[:attrs "transcript_id"])
        gene-id      (get-in repr '[:attrs "gene_id"])
        gene-name    (get-in repr '[:attrs "gene_name"])
        tags         (get-in repr '[:attrs "tag"])
        type         (get-in repr '[:attrs "transcript_type"])
        gene-type    (get-in repr '[:attrs "gene_type"])]
    [(:seq-name repr)
     (- (:start (first exons)) 1)
     (:end (last exons))
     trans-id
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
     trans-id
     "unk"
     "unk"
     (str/join "," 
       (for [e exons]
         (exon-phase e cds)))                     
     (or type "-")
     (or gene-id trans-id)
     (or gene-name gene-id trans-id)
     (or gene-type "-")
     (or tags "-")]))

(defn gtf2beds [gff & {:keys [conv] :or {conv gtf2bed}}]
  (->>
  (let [{genes        "gene" 
         transcripts  "transcript" 
         exons        "exon" 
         cds          "CDS"} (group-by :type gff)
        exons-by-id (group-by #(get-in % '[:attrs "transcript_id"]) exons)
        cds-by-id (group-by #(get-in % '[:attrs "transcript_id"]) cds)]
    (if (empty? transcripts)
      (for [[tid el] exons-by-id]
        (conv (first el) el (cds-by-id tid)))
      (for [t transcripts :let [tid (get-in t '[:attrs "transcript_id"])]]
        (conv t (exons-by-id tid) (cds-by-id tid)))))
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
