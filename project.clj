(defproject clj-scripts "0.1.0-SNAPSHOT"
  :description "clojure utils"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [commons-net/commons-net "3.6"]
                 [org.clojure/core.async "0.4.490"]
                 ;; http client
                 [clj-http "3.9.1"]
                 ;; Html templating
                 [selmer "1.12.11"]
                 ]
  :profiles {:dev {:dependencies [[midje "1.9.6"]]}}
  :main script.core
  :aot [script.core])
