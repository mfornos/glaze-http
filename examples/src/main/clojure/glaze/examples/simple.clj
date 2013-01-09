(ns
  #^{:doc "Simple Glaze usage patterns from Clojure"}
  glaze.examples.simple
  (:import 
    (glaze.client.handlers ErrorHandler)
    (org.apache.http.client ResponseHandler)
    (org.apache.http.util EntityUtils))
  (:use clojure.contrib.import-static)
  (:gen-class))

(import-static glaze.Glaze Get Post)

;; $ lein run http://www.ask.com

;; ok, clean dot dot instead of -> . :P
(defn map-request [uri]
  "Maps a Get request with an ErrorHandler"
  (.. (Get uri)
        (withErrorHandler 
          (reify ErrorHandler
            (onError[this response] 
              (println (str response " error went through handler")))))
        map))

(defn send-request-rh [uri]
  "Sends a Get request with a ResponseHandler"
  (.. (Get uri)
        (withHandler 
          (reify ResponseHandler
            (handleResponse[this response] 
              (println (str response " went through handler"))
              (EntityUtils/toString (.getEntity response)))))
        send))

(defn send-request-eh [uri]
  "Sends a Get request with an ErrorHandler"
  (.. (Get uri)
        (withErrorHandler 
          (reify ErrorHandler
            (onError[this response] 
              (println (str response " error went through handler")))))
        send))

(defn send-request [uri]
  "Sends a Get request"
  (.. (Get uri) send))


(defn -main [uri]
  (println 
    (send-request uri)))
