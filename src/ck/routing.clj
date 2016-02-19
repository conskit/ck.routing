(ns ck.routing
  (:require
    [clojure.tools.logging :as log]
    [puppetlabs.trapperkeeper.core :refer [defservice]]
    [puppetlabs.trapperkeeper.services :refer [service-context]]))

(defprotocol CKRouter
  "Router functions"
  (make-ring-hanlder [this make-fn]))

(defservice
  router CKRouter
  [[:ActionRegistry select-meta-keys get-action]]
  (start [this context]
         (log/info "Starting Router")
         (-> context
             (assoc :routes (filter #(not-empty %) (select-meta-keys [:route :id])))))
  (make-ring-hanlder [this make-fn]
                     (make-fn (:routes (service-context this)) get-action)))
