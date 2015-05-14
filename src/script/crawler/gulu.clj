(ns hunter.crawler.gulu
  "Candidates crawler for gulu"
  (:require [clj-http.client :as http]
            [clj-http.cookies :as cookie]))

(def ^:private cookie (cookie/cookie-store))
(defn- get-url
  "Concatenate url"
  [host uri]
  (str "http://" host uri))
(defn- param
  "Merge default param with provided param"
  [param-map]
  (merge {:cookie-store cookie} param-map))

(defn login
  [host email passwd]
  (let [json-param {:data {:email email
                           :password passwd
                           :remember "false"
                           :next ""}}]
    (http/post (get-url host "/rest/user/login")
               (param {:form-params json-param}))))
