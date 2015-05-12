(ns script.ssh.core
  (:require [clojure.string :as string]
            [script.config :refer [config]]
            [script.util :as util]))

(def ^:private conf (delay (:ssh @config)))

(defn ls
  "List all ssh servers"
  [& args]
  (let [len (apply max
                   (map #(count (name %)) (keys (apply hash-map @conf))))]
    (util/out (->> (partition 2 @conf)
                   (map (fn [[key value :as entry]]
                          (format (str "%" len "s" ": %s") (name key) value)))
                   (string/join \newline)))))

(defn g
  "Get IP of a server"
  [key]
  (util/out ((keyword key) (apply hash-map @conf))))

(defn s
  "SSH to a server"
  [key & args]
  (let [username (or (first args) "$(whoami)")
        ip (or ((keyword key) (apply hash-map @conf)) key)]
    (util/exec (str "ssh " username "@" ip))))
