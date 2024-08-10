(ns server
  (:require [ruuter.core :as ruuter]
            [ring.adapter.jetty :as jetty]
            [ring.util.http-response :as http-response]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.core :as h]
            [hiccup.page :as p]
            [clojure.pprint]

            [tablecloth.api :as tc]
            [scicloj.clay.v2.api :as clay]
            [scicloj.kindly.v4.kind :as kind]

            [ns-with-dataset]
            [clay-examples]))


;; HTMX helper functions from https://github.com/kit-clj/modules/blob/master/htmx/assets/src/htmx.clj

(defn page "Renders full page" [opts & content]
  (-> (p/html5 opts content)
      http-response/ok
      (http-response/content-type "text/html")))

(defn ui "Renders HTMX partial" [opts & content]
  (-> (h/html opts content)
      http-response/ok
      (http-response/content-type "text/html")))


(defn kind->div "Converts Clay Kind into a single hiccup div" [kind]
  (->> (clay/make-hiccup {:single-value kind})
       ;; TODO experiment with :inline-js-and-css true
       (into [:div])))


;; Define views as in https://github.com/kit-clj/modules/blob/master/htmx/assets/src/ui.clj

;; TODO separate view with :inline-js-and-css and no css loaded on toplevel
(defn home "Initial view" [request]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "Clay + Htmx demo"]
    [:script {:src "https://unpkg.com/htmx.org@1.9.10/dist/htmx.min.js" :defer true}]
    [:script {:src "https://unpkg.com/hyperscript.org@0.9.12" :defer true}] ; not used for this demo
    [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.16.9/katex.min.js" :defer true}]
    [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/vega-embed/6.26.0/vega-embed.js" :defer true}]
    [:script {:src "https://cdn.jsdelivr.net/npm/echarts@5.4.1/dist/echarts.js" :defer true}] ; demos not working with this
    [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.30.2/cytoscape.min.js" :defer true}]
    [:script {:src "https://code.highcharts.com/highcharts.js" :defer true}]
    [:script {:src "https://cdn.plot.ly/plotly-2.34.0.min.js" :defer true}]] ;; clay "inline" plotly reference fails
   [:script {:src "https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.js" :defer true}]

   [:body

    [:h1 "Clay Ring HTMX demo (without :inline-js-and-css)"]

    [:form {:hx-post "/clay-demo" :hx-target "#results-div" :hx-swap "innerHTML"}
     [:p "Select <a href='https://scicloj.github.io/clay/clay_book.examples.html'
                    target='_blank'>Clay example</a> to render in #results-div beneath"]
     [:select {:name "form-input"}
      (for [item (keys clay-examples/kind-example-fns)]
        [:option {:value item} item])]
     [:button {:type "submit"} "Render"]]

    ;; [:form {:hx-post "/product-history" :hx-target "#results-div" :hx-swap "innerHTML"}
    ;;  [:p "Enter data to process on the server and replace #results-div"]
    ;;  [:input {:type "text" :name "form-input"}]  ; could pass
    ;;  [:button {:type "submit"} "Render"]]

    [:div#results-div]
    ]))

(defn clay-demo-partial [request]
  (println (clojure.pprint/pprint request))
  (let [form-input (get (:form-params request) "form-input")
        kind-fn ((keyword form-input) clay-examples/kind-example-fns)]
    (ui
     (kind->div (kind-fn))
     ;;[:div (str clojure.pprint/pprint (first data))]
     )
    ))

(defn product-history [request]
  (println (clojure.pprint/pprint request))
  (let [form-input (get (:form-params request) "form-input")
        data ns-with-dataset/ds]
    (ui
     [:div (str clojure.pprint/pprint (first data))]
     )
    ))

(def routes [{:path "/"
              :method :get
              :response home ;;{:status 200 :body "Hi there!"}
              }
             {:path "/clay-demo"
              :method :post
              :response clay-demo-partial}

             {:path "/product-history"
              :method :post
              :response product-history}
             ])


;; Simple server setup reference:
;; - https://ericnormand.me/guide/clojure-web-tutorial
;; - https://github.com/askonomm/ruuter?tab=readme-ov-file#setting-up-with-ring--jetty

;; TODO changing view functions on runtime not working

(defonce server (atom nil))

(def app (-> #(ruuter/route routes %)
             wrap-params))  ; wrap-params makes form data accessible in request map

(defn start-server []
  (reset! server
          (jetty/run-jetty app {:port 3000 :join? false})))

(defn stop-server []
  (when-some [s @server] ;; check if there is an object in the atom
    (.stop s)            ;; call the .stop method
    (reset! server nil)));; overwrite the atom with nil

(defn -main []
  (start-server)
  )

(-main)
(comment
  (stop-server)

  ns-with-dataset/ds

  )
