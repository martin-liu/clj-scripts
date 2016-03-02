(ns script.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))
(def ^:const conf-file (str (System/getProperty "user.home") "/.config.edn"))
(def config
  (delay (edn/read-string
          (slurp (or
                  (System/getProperty "CONFIG_FILE")
                  conf-file)))))
