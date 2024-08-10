(ns ns-with-dataset
  (:require [tablecloth.api :as tc])
  )

;; https://scicloj.github.io/tablecloth/#dataset-creation
(defonce ds (tc/dataset "https://vega.github.io/vega-lite/examples/data/seattle-weather.csv"))
