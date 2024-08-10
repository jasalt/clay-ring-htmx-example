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



;; Define views as in https://github.com/kit-clj/modules/blob/master/htmx/assets/src/ui.clj


;;;; View 1 testing without :inline-js-and-css

(defn clay-demo-view "Initial view with Clay dependencies included as separate script tags from CDN's" [request]
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

    [:h4 "Clay Ring HTMX demo (without :inline-js-and-css)"]
    [:a {:href "/inline-js-and-css"} "Enable :inline-js-and-css"]

    [:form {:hx-post "/echo" :hx-target "#results-div" :hx-swap "innerHTML"}
     [:input {:type "text" :name "form-input"}]
     [:button {:type "submit"} "Dummy echo test"]]

    [:form {:hx-post "/clay-demo-partial" :hx-target "#results-div" :hx-swap "innerHTML"}
     [:p "Select <a href='https://scicloj.github.io/clay/clay_book.examples.html'
                    target='_blank'>Clay example</a> to render in #results-div beneath"]

     [:select {:name "form-input"}
      (for [item (keys clay-examples/kind-example-fns)]
        [:option {:value item} item])]
     [:button {:type "submit"} "Render"]]

    [:div#results-div]
    ]))

(defn kind->div
  "Converts Clay Kind into a single hiccup div"
  [kind]
  (->> (clay/make-hiccup {:single-value kind})
       (into [:div])))

(defn clay-demo-partial [request]
  (println (clojure.pprint/pprint request))
  (let [form-input (get (:form-params request) "form-input")
        kind-fn ((keyword form-input) clay-examples/kind-example-fns)]
    (ui
     (kind->div (kind-fn)))
    ))


;;;; View 2. testing :inline-js-and-css

(defn clay-demo-inline-view "View testing :inline-js-and-css" [request]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "Clay + Htmx demo"]
    [:script {:src "https://unpkg.com/htmx.org@1.9.10/dist/htmx.min.js" :defer true}]
    [:script {:src "https://unpkg.com/hyperscript.org@0.9.12" :defer true}] ; not used for this demo
    ]
   [:body

    [:h4 "Clay Ring HTMX demo (with :inline-js-and-css)"]
    [:a {:href "/"} "Disable :inline-js-and-css"]

    [:form {:hx-post "/echo" :hx-target "#results-div" :hx-swap "innerHTML"}
     [:input {:type "text" :name "form-input"}]
     [:button {:type "submit"} "Dummy echo test"]]

    [:form {:hx-post "/clay-demo-inline-partial" :hx-target "#results-div" :hx-swap "innerHTML"}
     [:p "Select <a href='https://scicloj.github.io/clay/clay_book.examples.html'
                    target='_blank'>Clay example</a> to render in #results-div beneath"]
     [:select {:name "form-input"}
      (for [item (keys clay-examples/kind-example-fns)]
        [:option {:value item} item])]
     [:button {:type "submit"} "Render"]]

    [:div#results-div]
    ]))


(defn kind->div-inline
  "Converts Clay Kind into a single hiccup div with inline assets.
   Duplicate `kind->div` code for clarity."
  [kind]
  (->> (clay/make-hiccup {:single-value kind :inline-js-and-css true})
       (into [:div])))


(defn clay-demo-inline-partial [request]
  (println (clojure.pprint/pprint request))
  (let [form-input (get (:form-params request) "form-input")
        kind-fn ((keyword form-input) clay-examples/kind-example-fns)]
    (ui
     (kind->div-inline (kind-fn)))))


;;;; Dummy echo endpoint

(defn echo [request]
  (println (clojure.pprint/pprint request))
  (let [form-input (get (:form-params request) "form-input")]
    (ui [:h1 form-input])))


;;;; Routes

(def routes [;; View 1
             {:path "/"
              :method :get
              :response clay-demo-view ;;{:status 200 :body "Hi there!"}
              }
             {:path "/clay-demo-partial"
              :method :post
              :response clay-demo-partial}

             ;; View 2
             {:path "/inline-js-and-css"
              :method :get
              :response clay-demo-inline-view
              }
             {:path "/clay-demo-inline-partial"
              :method :post
              :response clay-demo-inline-partial}

             ;; Dummy view
             {:path "/echo"
              :method :post
              :response echo}
             ])


;;;;; Simple server setup

;; Reference:
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
  )
