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

(defn home "Initial view" [request]
  (page
   [:head
    [:meta {:charset "UTF-8"}]
    [:title "Clay + Htmx demo"]
    [:script {:src "https://unpkg.com/htmx.org@1.9.10/dist/htmx.min.js" :defer true}]
    [:script {:src "https://unpkg.com/hyperscript.org@0.9.12" :defer true}] ; not used for this demo
    [:script {:src "https://cdn.jsdelivr.net/npm/echarts@5.4.1/dist/echarts.js" :defer true}] ; demos not working with this
    [:script {:src "https://cdn.plot.ly/plotly-2.34.0.min.js" :defer true}]] ;; clay "inline" plotly reference fails
   [:body

    [:h1 "Clay Ring HTMX demo"]

    [:form {:hx-post "/product-history" :hx-target "#results-div" :hx-swap "innerHTML"}
     [:input {:type "text" :name "form-input"}]
     [:button {:type "submit"} "SEND"]]

    [:button {:hx-post "/clicked" :hx-target "#results-div" :hx-swap "innerHTML"} "Show random Plotly plot"]
    [:div#results-div]
    ]))


(defn clicked "Demo route" [_]
  (ui
   (kind->div (:plotly clay-examples/kind-examples))))

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
             {:path "/clicked"
              :method :post
              :response clicked
              }
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

(comment
  (stop-server)

  ns-with-dataset/ds

  )
