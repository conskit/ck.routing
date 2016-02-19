(ns ck.routing
  (:require
    [clojure.tools.logging :as log]
    [puppetlabs.trapperkeeper.core :refer [defservice]]
    [puppetlabs.trapperkeeper.services :refer [service-context]]))

(defn make-ring-handler*
  "Creates a ring handler by calling the provided function on the routes provided"
  [routes make-fn get-action]
  (make-fn routes get-action))

(defprotocol CKRouter
  "Router functions"
  (make-ring-handler [this make-fn]))

(defservice
  router CKRouter
  [[:ActionRegistry select-meta-keys get-action]]
  (start [this context]
         (log/info "Starting Router")
         (-> context
             (assoc :routes (filter #(not-empty %) (select-meta-keys [:route :id])))))
  (make-ring-handler [this make-fn]
                     (make-ring-handler* (:routes (service-context this)) make-fn get-action)))
