(ns ck.routing
  (:require
    [clojure.tools.logging :as log]
    [puppetlabs.trapperkeeper.core :refer [defservice]]
    [puppetlabs.trapperkeeper.services :refer [service-context]]))

(defmulti make-ring-handler* :provider)

(defprotocol CKRouter
  "Router functions"
  (make-ring-handler [this provider]))

(defservice
  router CKRouter
  [[:ActionRegistry select-meta-keys get-action]]
  (start [this context]
         (log/info "Starting Router")
         (-> context
             (assoc :routes (filter #(not (nil? (:route %)))
                                    (select-meta-keys [:route :id])))))
  (make-ring-handler [this provider]
                     (make-ring-handler* (merge {:provider   provider
                                                 :get-action get-action}
                                                (service-context this)))))
