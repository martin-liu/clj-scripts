(ns script.core
  (:gen-class)
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as string]
            [script.ssh.core :as ssh]
            [script.mibox.core :as mibox]
            [script.util :as util]))

;;; Privates
(def ^:private ^:const m-ns 'script.core)

;; function generator
(defn- generate [cmd]
  (fn self
    ([prefix] (self prefix "-h"))
    ([prefix command & args]
     (let [the-ns (symbol (str "script." cmd ".core"))]
       (if-let [f (ns-resolve the-ns (symbol command))]
         (apply f args)
         (util/out (util/usage (str prefix " " cmd) the-ns)))))))

;;; Publics
(def ssh
  "ssh utlities"
  (generate "ssh"))

(def mibox
  "Mibox TV custom channels management"
  (generate "mibox"))

(defn -main
  ([prefix] (-main prefix "-h"))
  ([prefix command & args]
   (if-let [f (ns-resolve m-ns (symbol command))]
     (apply f (conj args prefix))
     (util/out (util/usage prefix m-ns)))))
