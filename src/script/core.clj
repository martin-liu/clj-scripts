(ns script.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [script.ssh.core :as ssh]
            [script.util :as util]))

;;; Privates
(def ^:private ^:const m-ns 'script.core)

;;; Publics
(defn ssh
  "ssh utlities"
  ([prefix] (ssh prefix "-h"))
  ([prefix command & args]
   (let [the-ns 'script.ssh.core]
     (if-let [f (ns-resolve the-ns (symbol command))]
       (apply f args)
       (println (util/usage (str prefix " ssh") the-ns))))))

(defn mibox
  "Mibox TV custom channels management"
  ([] (mibox "-h"))
  ([& args]
   ))

(defn -main
  ([prefix] (-main prefix "-h"))
  ([prefix command & args]
   (if-let [f (ns-resolve m-ns (symbol command))]
     (apply f (conj args prefix))
     (println (util/usage prefix m-ns)))))
