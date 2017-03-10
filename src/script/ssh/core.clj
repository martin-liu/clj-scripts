(ns script.ssh.core
  (:require [clojure.string :as string]
            [script.config :refer [config]]
            [script.util :as util]))

(def ^:private conf (delay (:ssh @config)))
(defn ^:private get-server
  "get server from the key"
  [key]
  (or ((keyword key) (apply hash-map @conf)) key))

(defn ^:command ls
  "List all ssh servers"
  [& args]
  (let [len (apply max
                   (map #(count (name %)) (keys (apply hash-map @conf))))]
    (util/out (->> (partition 2 @conf)
                   (map (fn [[key value :as entry]]
                          (format (str "%" len "s" ": %s") (name key) value)))
                   (string/join \newline)))))

(defn ^:command g
  "Get IP of a server"
  [key]
  (util/out (get-server key)))

(defn ^:command s
  "SSH to a server or exec command: `[user@]host-key [command]`"
  [keystr & args]
  (let [match (re-find #"(.*)@(.*)" keystr) ; check if `user@host-key` style
        username (or (and match
                          (= 3 (count match))
                          (second match)) ; if `user@host-key`, then parse user, otherwise use `$(whoami)`
                     "$(whoami)")
        key (or (and match
                     (= 3 (count match))
                     (last match))      ; if `user@host-key`, then parse `host-key`, otherwise use whole string as host key
                keystr)
        ip (get-server key)]
    (util/exec (str "ssh " username "@" ip " \"" (string/join " " args) " \"")))) ; append all other args to command in `" $args "` style

(defn ^:command cp
  "Scp file"
  [arg1 arg2 & args]
  (let [username (or (first args) "$(whoami)")
        get-key (fn [path]
                  (let [match (re-find #"(.*):.*" path)]
                    (if (and match
                             (= 2 (count match)))
                      (last match)
                      path)))
        replace-key (fn [path]
                      (let [key (get-key path)
                            replace-str (str username "@" (get-server key))]
                        (if (= key path)
                          path
                          (.replace path key replace-str))))
        path1 (replace-key arg1)
        path2 (replace-key arg2)]
    (util/exec (str "scp -r " path1 " " path2))))
