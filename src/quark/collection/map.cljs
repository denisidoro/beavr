(ns quark.collection.map
  (:require [clojure.walk :as walk]))

(defn find-first [pred coll] (first (filter pred coll)))

(defn filter-keys [fun coll] (into {} (filter (fn [[k _]] (fun k)) coll)))
(defn filter-vals [fun coll] (into {} (filter (fn [[_ v]] (fun v)) coll)))

(defn map-keys [f m] (into {} (for [[k v] m] [(f k) v])))
(defn map-vals [f m] (into {} (for [[k v] m] [k (f v)])))

(defn ^:private throw-exception
  [message]
  nil)

(defn assoc-if
  "Assoc[iate] only truthy values."
  ([m k v]
   (-> m (cond-> v (assoc k v))))
  ([m k v & kvs]
   (let [ret (assoc-if m k v)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw-exception "assoc-if expects even number of arguments after map/vector, found odd number"))
       ret))))

(defn assoc-some
  "Assoc[iate] if the value is not nil."
  ([m k v]
   (if (nil? v) m (assoc m k v)))
  ([m k v & kvs]
   (let [ret (assoc-some m k v)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw-exception "assoc-some expects even number of arguments after map/vector, found odd number"))
       ret))))

(defn assoc-in-if [m ks v]
  "Associates a truthy value in a nested associative structure"
  (-> m (cond-> v (assoc-in ks v))))

(defn assoc-in-some [m ks v]
  "Associates a value in a nested associative structure,
  if the value is not nil"
  (if (nil? v) m (assoc-in m ks v)))

(defn dissoc-in [m key-vec]
  (let [firsts (vec (butlast key-vec))
        node   (dissoc (get-in m firsts) (last key-vec))]
    (assoc-in-if m firsts node)))

(defn dissoc-if [m k pred]
  (cond-> m (pred (get m k)) (dissoc k)))

(defn dissoc-in-if [m ks pred]
  (cond-> m (pred (get-in m ks)) (dissoc-in ks)))

(defn ^:private contains-in? [m ks]
  (not= ::absent (get-in m ks ::absent)))

(defn update-in-if [m ks f & args]
  (if (contains-in? m ks)
    (apply (partial update-in m ks f) args)
    m))

(defn depth-map-keys [func m]
  "Apply `func` to all keys from `m`"
  (let [f (fn [[k v]] (if (keyword? k) [(func k) v] [k v]))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn deep-merge
  "Recursively merges maps.
   If the first parameter is a keyword it tells the strategy to
   use when merging non-map collections. Options are
   - :replace, the default, the last value is used
   - :into, if the value in every map is a collection they are concatenated
     using into. Thus the type of (first) value is maintained."
  {:arglists '([strategy & values] [values])}
  [& values]
  (let [[values strategy] (if (keyword? (first values))
                            [(rest values) (first values)]
                            [values :replace])]
    (cond (every? map? values)
          (apply merge-with (partial deep-merge strategy) values)
          (and (= strategy :into) (every? coll? values)) (reduce into values)
          :else (last values))))

(defn safe-get
  [coll key & args]
  (if (coll? key) (apply get-in coll (vec key) args) (apply get coll key args)))

(defn pull-key
  [x key]
  (-> (dissoc x key)
      (merge (get x key))))

(defn leaf
  [m]
  (if (map? m)
    (recur ((-> m keys first) m))
    m))
