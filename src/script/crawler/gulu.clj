(ns script.crawler.gulu
  "Candidates crawler for gulu"
  (:require [clojure.core.async :as a]
            [clj-http.client :as http]
            [clj-http.cookies :as cookie]
            [selmer.parser :refer :all]
            [script.config :refer [config]]))

(def ^:private conf (get-in @config [:crawler :gulu]))
(def ^:private cookie (cookie/cookie-store))
(def ^:private user (promise))
(def ^:private html-tpl "gulu/resume.tpl.html")

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
   (if-let [body (:body (method (get-url uri)
                                (param parameters)))]
     (try (http/json-decode body
                            ;; make key to be keyword rather than string
                            true)
          (catch Exception e
            ;; if not json, then return raw data
            body)))))

(defn- save-file [cand]
  (let [file-name (clojure.string/join "_"
                                       [(get-in (first (:experiences cand))
                                                [:client :name])
                                        (:chineseName cand)
                                        (:title cand)
                                        (:mobile cand)])
        file-path (str (:output-dir conf) file-name ".html")]
    (spit file-path (render-file html-tpl cand))))

(defn- login
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
       (deliver user (:user (request "/rest/user/context")))))))

(defn- with-login
  "Make sure login first before call function"
  ([f do-login?]
   (do (login)
       (f)))
  ([f]
   (if (realized? user)
     (let [body (f)]
       (if (and (= false (:status body))
                (= "login required" (:message body)))
         (with-login f true)
         body))
     (with-login f true))))

(defn get-all-candidates
  ([] (get-all-candidates 1))
  ([page]
   (with-login
     #(request "/rest/candidate/list"
               {:query-params {
                               :user_id (:id @user)
                               :page page
                               :current "latest"
                               :byfilter 8937
                               :ordering "-dateAdded"
                               :paginate_by 500
                               }}))))

(defn retrieve-candidate
  [cand-id]
  (with-login
    #(let [cand (future (request (str "/rest/candidate/" cand-id)))
           notes (future (request "/rest/note/list"
                                  {:query-params {
                                                  :paginate_by 200
                                                  :external_type "candidate"
                                                  :external_id cand-id
                                                  }}))
           attachments (vec (pmap
                             (fn [attachment]
                               (if-let [uuid (:uuidname attachment)]
                                 (request (str "/rest/file/preview/" uuid))))
                             (:attachments @cand)))]
       (merge @cand {:notes @notes
                     :attachhtmls attachments}))))

(defn spide-go
  [pages]
  (let [num (atom 0)]
    (println "Starting...")
    (time (dotimes [page (Integer/parseInt pages)]
            (pmap
             #(do
                (swap! num inc)
                (save-file (retrieve-candidate (:id %))))
             (:list (get-all-candidates (inc page))))
            (println "Processed" @num "resumes")
            (flush)))
    (println "Done. Saved" @num "resumes to" (:output-dir conf))))
