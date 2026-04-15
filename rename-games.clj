(require '[clojure.string :as str])

(defn read-lines [file]
  (->>
   (slurp file)
   (str/split-lines)))

(defn read-game-list [file]
  (into [] (read-lines file)))

(defn read-games [file-names]
  (map read-lines file-names))

(defn find-line-containing [field game]
  (->>
   game
   (filter
    (fn [line] (str/includes? line field)))
   (first)))

(defn remove-tag [line field]
  (->
   line
   (str/replace (str "[" field " ") "")
   (str/replace "]" "")
   (str/replace "\"" "")))

(defn value-for [field game]
  (let [line (find-line-containing field game)]

    (if-not (nil? line)
      (remove-tag line field))))

(defn parse-date [string]
  (let [dotted-pattern
        (java.time.format.DateTimeFormatter/ofPattern "yyyy.MM.dd")

        dashed-pattern
        (java.time.format.DateTimeFormatter/ofPattern "dd/MM/yyyy")]

    (try
      (java.time.LocalDate/parse string dotted-pattern)
      (catch
          java.time.format.DateTimeParseException exception
          (java.time.LocalDate/parse string dashed-pattern)))))

(defn parse-round [string]
  (if (or (nil? string) (= string "-")) nil
      (Integer/parseInt string)))

(defn extract-opponent [lines]
  (let [white (value-for "White" lines)
        black (value-for "Black" lines)]
    (if (str/includes? white "Chorley") black white)))

(defn to-game [lines]
  {:date (->> lines (value-for "Date") (parse-date))
   :round (->> lines (value-for "Round") (parse-round))
   :opponent (extract-opponent lines)})

(defn parse-them [games]
  (map to-game games))

(defn as-file-name [game]
  (str
   (:date game)

   (if (not (nil? (:round game)))
     (str "-r" (:round game))
     "")

   "-"
   (str/replace (:opponent game) " " "-")

   ".pgn"))

(defn as-new-file-names [games]
  (map as-file-name games))

(defn pair-with [old-file-names new-file-names]
  (zipmap old-file-names new-file-names))

(let [old-file-names (read-game-list "game-list")]
  (->>
   old-file-names
   (read-games)
   (parse-them)
   (as-new-file-names)
   (pair-with old-file-names)))
