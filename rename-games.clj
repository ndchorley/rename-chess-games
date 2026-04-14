(require '[clojure.string :as str])

(defn read-lines [file]
  (->>
   (slurp file)
   (str/split-lines)))

(defn read-games [file-names]
  (map read-lines file-names))

(defn value-for [field game]
  (let [line
        (->>
         game
         (filter
          (fn [line] (str/includes? line field)))
         (first))]
    
    (->
     line
     (str/replace (str "[" field " ") "")
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
  {:date (->> lines (value-for "Date") (parse-date)) })

(defn parse-them [games]
  (map to-game games))

(->>
 "game-list"
 (read-lines)
 (read-games)
 (parse-them))
