(require '[clojure.string :as str])

(defn read-lines [file]
  (->>
   (slurp file)
   (str/split-lines)))

(defn read-game-list [file]
  (read-lines file))

(defn read-games [file-names]
  (map read-lines file-names))

(defn value-for [field game]
  (let [line
        (->>
         game
         (filter
          (fn [line] (str/includes? line field)))
         (first))]

    (if-not (nil? line)
      (->
       line
       (str/replace (str "[" field " ") "")
       (str/replace "]" "")
       (str/replace "\"" "")))))

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

(->>
 "game-list"
 (read-game-list)
 (read-games)
 (parse-them))
