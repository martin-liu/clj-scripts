(ns script.ssh.core
  (:require [clojure.pprint :as pprint]
            [script.config :refer [config]]
            [script.util :as util]))

(def ^:private conf (delay (:ssh @config)))

(defn ls
  "List all ssh servers"
  [& args]
  (println @conf)
  (let [len (apply max
                   (map #(count (name %)) (keys @conf)))]
    (doseq [[key value] @conf]
      (printf (str "%" len "s" ": %s\n") (name key) value))))

(defn g
  "Get IP of a server"
  [key]
  (print ((keyword key) @conf)))

(defn s
  "SSH to a server"
  [key & args]
  (let [username (or (first args) "$(whoami)")
        ip (or ((keyword key) @conf) key)]
    (util/exec (str "ssh " username "@" ip))))
