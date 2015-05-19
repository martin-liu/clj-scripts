(ns script.crawler.gulu
  "Candidates crawler for gulu"
  (:require [clojure.core.async :refer :all]
            [clj-http.client :as http]
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
(defn- request
  "request a URI"
  ([uri] (request uri {}))
  ([uri parameters] (request uri parameters http/get))
  ([uri parameters method]
   (http/json-decode (:body (method (get-url uri)
                                    (param parameters))))))

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
       (deliver user (get-in (request "/rest/user/context")
                             ["user"]))))))

(defn with-login
  "Make sure login first before call function"
  ([f do-login?]
   (do (login)
       (f)))
  ([f]
   (if (realized? user)
     (let [body (f)]
       (if (and (= false (get body "status"))
                (= "login required" (get body "message")))
         (with-login f true)
         body))
     (with-login f true))))

(defn get-all-candidates
  ([] (get-all-candidates 1))
  ([page]
   (with-login
     #(request "/rest/candidate/list"
               {:query-params {
                               :user_id (get @user "id")
                               :page page
                               :current "latest"
                               :byfilter 8937
                               :ordering "-dateAdded"
                               :paginate_by 100
                               }}))))

(defn retrieve-candidate
  [cand-id]
  (with-login
    #(let [cand (request (str "/rest/candidate/" cand-id))]
       (request "/rest/note/list"
                {:query-params {
                                :paginate_by 200
                                :external_type "candidate"
                                :external_id cand-id
                                }}))))
