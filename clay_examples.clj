(ns clay-examples
  (:require
   [tablecloth.api :as tc]
   [scicloj.clay.v2.api :as clay]
   [scicloj.kindly.v4.kind :as kind]))

;; example code from https://scicloj.github.io/clay/clay_book.examples.html

(def people-as-maps
  (->> (range 29)
       (mapv (fn [i]
               {:preferred-language (["clojure" "clojurescript" "babashka"]
                                     (rand-int 3))
                :age (rand-int 100)}))))

(def people-as-vectors
  (->> people-as-maps
       (mapv (juxt :preferred-language :age))))

(def nested-structure-1
  {:vector-of-numbers [2 9 -1]
   :vector-of-different-things ["hi"
                                (kind/hiccup
                                 [:big [:big "hello"]])]
   :map-of-different-things {:markdown (kind/md ["*hi*, **hi**"])
                             :number 9999}
   :hiccup (kind/hiccup
            [:big [:big "bye"]])
   :dataset (tc/dataset {:x (range 3)
                         :y [:A :B :C]})})

(def kind-examples
  {
   :pretty-printing (kind/pprint nested-structure-1)
   :tables (kind/table
            {:column-names [:preferred-language :age]
             :row-vectors people-as-vectors})
   :cytoscape (kind/cytoscape
               {:elements {:nodes [{:data {:id "a" :parent "b"} :position {:x 215 :y 85}}
                                   {:data {:id "b"}}
                                   {:data {:id "c" :parent "b"} :position {:x 300 :y 85}}
                                   {:data {:id "d"} :position {:x 215 :y 175}}
                                   {:data {:id "e"}}
                                   {:data {:id "f" :parent "e"} :position {:x 300 :y 175}}]
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
                         :padding 5}})
   :plotly (kind/plotly
            {:data [{:x [0 1 3 2]
                     :y [0 6 4 5]
                     :z [0 8 9 7]
                     :type :scatter3d
                     :mode :lines+markers
                     :opacity 0.5
                     :line {:width 5}
                     :marker {:size 4
                              :colorscale :Viridis}}]
             :layout {:title "Plotly example"}})
   })
