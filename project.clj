(defproject clj-scripts "0.1.0-SNAPSHOT"
  :description "clojure utils"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.7"]
                 [commons-net/commons-net "3.5"]
                 [org.clojure/core.async "0.4.474"]
                 ;; http client
                 [clj-http "3.9.0"]
                 ;; Html templating
                 [selmer "1.11.7"]
                 ]
  :profiles {:dev {:dependencies [[midje "1.9.1"]]}}
  :main script.core
  :aot [script.core])
