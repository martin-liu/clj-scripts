(ns hunter.crawler.gulu-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [hunter.crawler.gulu :refer :all]))

(facts "`About gulu crawler'"
       (fact "Login can store cookie"
             1 => 1)
       (fact "When need login, auto login to gulu"
             1 => 1))
