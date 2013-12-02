(ns genes.utils
  (:use clojure.java.io))

(defn parse-int [str]
  (Integer/parseInt str))

(defn parse-double [str]
  (Double/parseDouble str))

(defmacro with-outfile [f & body]
  (let [fh (gensym)]
    `(with-open [~fh (writer ~f)]
       (binding [*out* ~fh]
	 ~@body))))

(defn mapconj [m k v]
  (assoc m k (conj (or (get m k) []) v)))

(defn map-by [f xs]
  (reduce (fn [m x]
	    (mapconj m (f x) x))
	  {} xs))

(defmacro unless [condition & body]
    `(when (not ~condition)
       ~@body))

(defn fail [& args]
  (binding [*out* *err*]
    (apply println args)
    (System/exit 1)))


(defn to-byte-buffer [f]
  "Return a ByteBuffer containing an mmap of f"
  (let [channel (.getChannel (new java.io.FileInputStream f))
	size (.size channel)]
    (.map channel java.nio.channels.FileChannel$MapMode/READ_ONLY 0 size)))



(defn toke [s]
  (loop [st    (java.util.StringTokenizer. s)
        toks  []]
    (if (.hasMoreTokens st)
      (recur st (conj toks (.nextToken st)))
      toks)))

(defn rand-pick [l]
  (nth l (rand-int (count l))))