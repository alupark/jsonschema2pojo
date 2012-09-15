(ns jsonschema2pojo.server
  (:use [jsonschema2pojo.bridge]
        [clojure.tools.logging :only [error]]
        [compojure.core :only [defroutes POST]]
        [compojure.route :only [not-found]]
        [ring.middleware.params])
  (:import [java.io ByteArrayInputStream]
           [com.fasterxml.jackson.databind ObjectMapper])
  (:gen-class))

(def object-mapper (ObjectMapper.))

(defroutes routes

  (POST "/generator" {params :params}
        (try
          (let [schema (.readTree object-mapper (params "schema"))
                classname (params "classname")
                config (post-params-based-config params)
                zip-bytes (generate schema classname config)]
            {:status 200
             :headers {"Content-Type" "application/zip"}
             :body (ByteArrayInputStream. zip-bytes)})
          (catch Exception e
            (error "Failed to generate schema" e)
            {:status 500
             :headers {"Content-Type" "text/html"}
             :body "<h1>Internal Server Error</h1>"})))

  (not-found "<h1>Not Found</h1>"))

(def app (wrap-params routes))
