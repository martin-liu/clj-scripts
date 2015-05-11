(ns script.core-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [script.core :refer :all]
            ))


(facts "about `script.core/-main'"
       (with-redefs-fn {#'script.util/usage (fn [& args]
                                              (conj args "usage"))
                        #'script.core/ssh (fn [& args]
                                            (conj args "ssh"))
                        #'script.util/out identity}
         #(fact "main function will call finded public function in script.core ns, if not found, then call `script.util/usage' to generate usage information"
                (#'script.core/-main "m" "ssh" "arg0")  => '("ssh" "m" "arg0")
                (#'script.core/-main "m" "notexist" "arg0")  => '("usage" "m" script.core))))

(facts "about `script.core/generate'"
       (with-redefs-fn {#'script.ssh.core/ls #(conj %& 'script.ssh.core/ls)
                        #'script.util/usage (fn [& args]
                                              (conj args "usage"))
                        #'script.util/out identity}
        #(fact "generated func will call specified function in concatenated ns"
               ((#'script.core/generate "ssh") "m" "ls" "any-arg") => '(script.ssh.core/ls "any-arg")
               ((#'script.core/generate "ssh") "m" "non-exist-command" "any-args") => '("usage" "m ssh" script.ssh.core))))
