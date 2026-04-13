(require '[clojure.string :as str])

(defn read-lines [file]
  (->>
   (slurp file)
   (str/split-lines)))

(defn read-games [file-names]
  (map read-lines file-names))

(defn extract-date [game]
  (filter
   (fn [line] (str/includes? line "Date"))
   game))

(defn to-game [lines]
  {:date (extract-date lines) })

(defn parse-them [games]
  (map to-game games))

(->>
 "game-list"
 (read-lines)
 (read-games)
 (parse-them))
