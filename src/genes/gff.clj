(ns genes.gff
  (:use clojure.java.io genes.utils)
  (:require [clojure.string :as str]))

(defrecord GFFRecord [seq-name type source ^Integer start ^Integer end score strand frame attrs])

(defn make-gff-record [seq-name type source ^Integer start ^Integer end score strand frame attrs]
  (GFFRecord. seq-name type source start end score strand frame attrs))

(defn- parse-attrs [attr-string]
  (into {} (for [attr (str/split attr-string #";")]
             (let [subs (str/split attr  #"[=,]")]
               (if (= (count subs) 1)
                 [attr 'nil]
                 [(first subs) (vec (rest subs))])))))
                   

(defn parse-gff [r]
  (for [l (line-seq r) :when (and (not (= l ""))
				  (not (= (first l) \#)))]
    (let [[seq-name source type start end score strand frame attrs] (toke l "\t")
          attr-map (when attrs (parse-attrs attrs))]
      (make-gff-record seq-name type source (parse-int start) (parse-int end) score strand frame attr-map))))

(defn- assoc-cat [m k v] 
  (let [o (m k)] 
    (assoc m k (if o (str o "," v) v))))

(defn- parse-attrs2 [attr-string]
  (reduce (fn [m [k v]] (assoc-cat m k v)) {} (for [[_ k v] (re-seq  #"(\w+) \"([^\"]+)\"" attr-string)] ;"
    [k v])))
                   

(defn parse-gff2 [r]
  (for [l (line-seq r) :when (and (not (= l ""))
          (not (= (first l) \#)))]
    (let [[seq-name source type start end score strand frame attrs] (str/split l #"\t")
          attr-map (when attrs (parse-attrs2 attrs))]
      (make-gff-record seq-name type source (parse-int start) (parse-int end) score strand frame attr-map))))


(defn map-by-attrib [gff key]
  (reduce (fn [m r]
	    (let [attrs (:attrs r)
		  vals (get attrs key)]
	      (if vals (reduce (fn [mm v] (mapconj mm v r)) m vals)
		  m)))
	  {} gff))

(defn printgff [gff]
  (doseq [g gff]
    (println (str (:seq-name g) \tab
		  (:source g)   \tab
		  (:type g)     \tab
		  (:start g)    \tab
		  (:end g)      \tab
		  (:score g)    \tab
		  (:strand g)   \tab
		  (:frame g)))))

