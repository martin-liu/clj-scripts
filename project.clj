(defproject clj-scripts "0.1.0-SNAPSHOT"
  :description "Martin's clojure scripts"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main script.core
  :aot [script.core])
