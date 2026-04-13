(require '[clojure.string :as str])

(defn read-lines [file]
  (->>
   (slurp file)
   (str/split-lines)))

(defn read-games [file-names]
  (map read-lines file-names))

(defn extract-date [game]
  (let [date-line
        (->>
         game
         (filter
          (fn [line] (str/includes? line "Date")))
         (first))]
    
    (->
     date-line
     (str/replace "[Date " "")
     (str/replace "]" "")
     (str/replace "\"" ""))))

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

(defn to-game [lines]
  {:date (->> lines (extract-date) (parse-date)) })

(defn parse-them [games]
  (map to-game games))

(->>
 "game-list"
 (read-lines)
 (read-games)
 (parse-them))
