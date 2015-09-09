(ns script.crawler.core
  "Web crawlers"
  (:require [script.crawler.gulu :as gulu]))

(defn ^:command gulu
  "Run gulp crawler"
  []
  (gulu/spide-go))
