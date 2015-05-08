(ns script.util
  (:require [clojure.string :as string]))

(defn- get-doc [the-var]
  (str "\t\t" (:doc (meta the-var))))

(defn usage [prefix the-ns]
  (let [publics (ns-publics the-ns)]
    (->> ["Scripts"
          ""
          (format "Usage: %s <command> [args|options]" prefix)
          ""
          "Commands:"
          (->>
           (keys publics)
           (filter #(not= (symbol "-main") %))
           (map #(string/join ["  " % (get-doc (% publics))]))
           (string/join \newline))
          ]
         (string/join \newline))))

(defn exec [command]
  (print (str "exec|" command)))
