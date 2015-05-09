(ns script.mibox.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [script.config :refer [config]]
            [script.util :as util])
  (:import (org.apache.commons.net.ftp FTPClient
                                       FTPReply)
           (java.nio.charset.StandardCharsets)))

(def ^:private conf (delay (:mibox @config)))
(def ^:private url (str "ftp://"
                        (:host @conf)
                        ":"
                        (:port @conf)
                        "/"
                        (:file @conf)))

(defn- get-channel-map []
  (apply hash-map
         (filter (complement empty?)
                 (->
                  (slurp url)
                  (string/split #"\||\n")
                  ))))

(defn- get-channel-str [channel-map]
  (->>
   channel-map
   (map (fn [[key value :as entry]]
          (str key "|" value)))
   sort
   (string/join \newline)))

(defn- save [content]
  (let [client (FTPClient.)
        _ (.connect client (:host @conf) (:port @conf))
        _ (.login client "anonymous" "any_passwd")
        success (.storeFile client
                            (:file @conf)
                            (io/input-stream
                             (.getBytes content java.nio.charset.StandardCharsets/UTF_8)))]
    (if success
      (println "save file success!")
      (println "save file failed! error code: " (.getReplyCode client)))))

(defn ls
  "List all channels"
  [& args]
  (println (slurp url)))

(defn add
  "Add/update a channel"
  [key value]
  (->
   (assoc (get-channel-map) key value)
   get-channel-str
   save))

(defn rm
  "Remove a channel"
  [key]
  (->
   (dissoc (get-channel-map) key)
   get-channel-str
   save))
