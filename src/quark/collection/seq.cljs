(ns quark.collection.seq)

(defn mmapcat
  [f coll]
  (mapcat #(map f %) coll))
