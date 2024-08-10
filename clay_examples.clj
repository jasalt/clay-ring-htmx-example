(ns clay-examples
  (:require
   [tablecloth.api :as tc]
   [scicloj.clay.v2.api :as clay]
   [scicloj.kindly.v4.kind :as kind]))

;; example code from https://scicloj.github.io/clay/clay_book.examples.html

(defn people-as-maps []
  (->> (range 29)
       (mapv (fn [i]
               {:preferred-language (["clojure" "clojurescript" "babashka"]
                                     (rand-int 3))
                :age (rand-int 100)}))))

(defn people-as-vectors []
  (->> (people-as-maps)
       (mapv (juxt :preferred-language :age))))

(defn nested-structure-1 []
  {:vector-of-numbers (vec (repeatedly 3 #(- (rand-int 21) 10)))
   :vector-of-different-things ["hi"
                                (kind/hiccup
                                 [:big [:big "hello"]])]
   :map-of-different-things {:markdown (kind/md ["*hi*, **hi**"])
                             :number 9999}
   :hiccup (kind/hiccup
            [:big [:big "bye"]])
   :dataset (tc/dataset {:x (range 3)
                         :y [:A :B :C]})})

(defn random-data [n]
  (->> (repeatedly n #(- (rand) 0.5))
       (reductions +)
       (map-indexed (fn [x y]
                      {:w (rand-int 9)
                       :z (rand-int 9)
                       :x x
                       :y y}))))

(defn randomize-v [v]
  (mapv #(+ % (rand-int 20000)) v)
  )

(def kind-example-fns "Map of fn's that return kinds with randomized data"
  {
   :pretty-printing (fn [] (kind/pprint (nested-structure-1)))
   :tables (fn [] (kind/table
                   {:column-names [:preferred-language :age]
                    :row-vectors (people-as-vectors)}))
   :data-table (fn [] (kind/table
                       {:use-datatables true
                        :column-names [:preferred-language :age]
                        :row-vectors (people-as-vectors)}))
   :vega-lite-fails (fn [] (kind/vega-lite {:data {:values (random-data 10)},
                                      :mark "point"
                                      :encoding
                                      {:size {:field "w" :type "quantitative"}
                                       :x {:field "x", :type "quantitative"},
                                       :y {:field "y", :type "quantitative"},
                                       :fill {:field "z", :type "nominal"}}}))
   :cytoscape (fn [] (kind/cytoscape
                      {:elements {:nodes [{:data {:id "a" :parent "b"} :position {:x (rand-int 300) :y (rand-int 10)}}
                                          {:data {:id "b"}}
                                          {:data {:id "c" :parent "b"} :position {:x (rand-int 300) :y (rand-int 10)}}
                                          {:data {:id "d"} :position {:x 215 :y 175}}
                                          {:data {:id "e"}}
                                          {:data {:id "f" :parent "e"} :position {:x (rand-int 300) :y (rand-int 200)}}]
                                  :edges [{:data {:id "ad" :source "a" :target "d"}}
                                          {:data {:id "eb" :source "e" :target "b"}}]}
                       :style [{:selector "node"
                                :css {:content "data(id)"
                                      :text-valign "center"
                                      :text-halign "center"}}
                               {:selector "parent"
                                :css {:text-valign "top"
                                      :text-halign "center"}}
                               {:selector "edge"
                                :css {:curve-style "bezier"
                                      :target-arrow-shape "triangle"}}]
                       :layout {:name "preset"
                                :padding 5}}))
   :echarts (fn [] (kind/echarts {:title {:text "Echarts Example"}
                                  :tooltip {}
                                  :legend {:data ["sales"]}
                                  :xAxis {:data ["Shirts", "Cardigans", "Chiffons",
                                                 "Pants", "Heels", "Socks"]}
                                  :yAxis {}
                                  :series [{:name "sales"
                                            :type "bar"
                                            :data (vec (repeatedly 4 #(rand-int 50)))}]}))
   :plotly (fn [] (kind/plotly
                   {:data [{:x (vec (repeatedly 4 #(rand-int 10)))
                            :y (vec (repeatedly 4 #(rand-int 10)))
                            :z (vec (repeatedly 4 #(rand-int 10)))
                            :type :scatter3d
                            :mode :lines+markers
                            :opacity 0.5
                            :line {:width 5}
                            :marker {:size 4
                                     :colorscale :Viridis}}]
                    :layout {:title "Plotly example"}}))
   :highcharts (fn [] (kind/highcharts
                       {:title {:text "Line chart"}
                        :subtitle {:text "By Job Category"}
                        :yAxis {:title {:text "Number of Employees"}}
                        :series [{:name "Manufacturing",
                                  :data (randomize-v [24916, 37941, 29742, 29851, 32490, 30282,
                                                      38121, 36885, 33726, 34243, 31050])}

                                 {:name "Sales & Distribution",
                                  :data (randomize-v [11744, 30000, 16005, 19771, 20185, 24377,
                                                      32147, 30912, 29243, 29213, 25663])}

                                 {:name "Operations & Maintenance",
                                  :data [nil, nil, nil, nil, nil, nil, nil,
                                         nil, 11164, 11218, 10077]}

                                 {:name "Other",
                                  :data (randomize-v [21908, 5548, 8105, 11248, 8989, 11816, 18274,
                                                      17300, 13053, 11906, 10073])}]

                        :xAxis {:accessibility {:rangeDescription "Range: 2010 to 2020"}}

                        :legend {:layout "vertical",
                                 :align "right",
                                 :verticalAlign "middle"}

                        :plotOptions {:series {:label {:connectorAllowed false},
                                               :pointStart 2010}}

                        :responsive {:rules [{:condition {:maxWidth 500},
                                              :chartOptions {:legend {:layout "horizontal",
                                                                      :align "center",
                                                                      :verticalAlign "bottom"}}}]}}))
   })
