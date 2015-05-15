(ns script.crawler.gulu
  "Candidates crawler for gulu"
  (:require [clj-http.client :as http]
            [clj-http.cookies :as cookie]
            [script.config :refer [config]]))

(def ^:private conf (get-in @config [:crawler :gulu]))
(def ^:private cookie (cookie/cookie-store))
(def ^:private user (promise))
(defn- get-url
  "Concatenate url"
  [uri & rest]
  (apply str (:base-url conf) uri rest))
(defn- param
  "Merge default param with provided param"
  [param-map]
  (merge {:cookie-store cookie} param-map))

(defn login
  ([] (login (:email conf) (:password conf)))
  ([email password]
   (let [json-param {:data (http/json-encode
                            {:email email
                             :password password
                             :remember "false"
                             :next ""})}
         ret (http/post (get-url "/rest/user/login")
                        (param {:form-params json-param}))]
     (if (= (:status ret) 200)
       (deliver user (get-in (http/json-decode
                              (:body (http/get (get-url "/rest/user/context")
                                               (param {}))))
                             ["user"]))))))

(defn with-login
  "Make sure login first before call function"
  [f]
  (if (realized? user)
    (let [ret (f)]
      ((http/json-decode (:body ret))))
    (do (login)
        (f))))

(defn get-all-candidates
  ([] (get-all-candidates 1))
  ([page]
   (with-login
     #(http/get (get-url "/rest/candidate/list")
                (param
                 {:query-params {
                                 :user_id (get @user "id")
                                 :page page
                                 :current "latest"
                                 :byfilter 8937
                                 :ordering "-dateAdded"
                                 :paginate_by 100
                                 }})))))

(defn retrieve-candidate
  [cand-id]
  (if-let (http/get (get-url "/rest/candidate/" cand-id))))
(defn download-file
  [url]
  )
(with-login
  #(http/get (get-url "/rest/candidate/" 125779)))
