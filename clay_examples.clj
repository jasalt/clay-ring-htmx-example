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

(def kind-example-fns "Map of fn's that return kinds with randomized data"
  {
   :pretty-printing (fn [] (kind/pprint (nested-structure-1)))
   :tables (fn [] (kind/table
                   {:column-names [:preferred-language :age]
                    :row-vectors (people-as-vectors)}))
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
   })
