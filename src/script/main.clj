(ns script.main
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]))

;;; Privates
(def ^:private ^:const m-ns 'script.main)
(defn- usage []
  (->> ["Scripts"
        ""
        "Usage: program-name <command> [actions|options]"
        ""
        "Commands:"
        (->>
         (keys (ns-publics m-ns))
         (filter #(not= (symbol "-main") %))
         (map #(string/join ["  " %]))
         (string/join \newline))
        ""
        "Use `program-name <command> -h' for more information."]
       (string/join \newline)))

;;; Publics
(defn ssh
  "ssh utlities"
  ([] (ssh "-h"))
  ([command & args]
   (println command args)
   (.exec (Runtime/getRuntime) command)))

(defn mibox
  "Mibox TV custom channels management"
  ([] (mibox "-h"))
  ([& args]
   ))

(defn -main
  ([] (-main "-h"))
  ([command & args]
   (if-let [f (ns-resolve m-ns (symbol command))]
     (apply f args)
     (println (usage)))))
