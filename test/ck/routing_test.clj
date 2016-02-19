(ns ck.routing-test
  (:require
    [puppetlabs.trapperkeeper.app :as app]
    [puppetlabs.trapperkeeper.core :refer [defservice]]
    [puppetlabs.trapperkeeper.services :refer [service-context]]
    [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-cli-data]]
    [ck.routing :refer [make-ring-handler*]]
    [conskit.core :as ck]
    [conskit.protocols :as ckp]
    [conskit.macros :refer :all])
  (:use midje.sweet))

(defn maker-fn
  [routes get-action-fn]
  (for [r routes
        :let [{:keys [id route]} r]]
    [route (get-action-fn id)]))

(fact (make-ring-handler* [{:id :my-action :route "/foo/bar"}]
                          maker-fn
                          #(identity %)) => [["/foo/bar" :my-action]])


(defcontroller
  my-controller
  []
  (action
    ^{:route "/hello/world"}
    my-action
    [req data]
    {:hello "world" :req req}))

(defprotocol ResultService
  (get-result [this]))

(defservice
  test-service ResultService
  [[:ActionRegistry register-controllers!]
   [:CKRouter make-ring-handler]]
  (init [this context]
        (register-controllers! [my-controller])
        context)
  (start [this context]
         {:result (make-ring-handler maker-fn)})
  (get-result [this]
              (:result (service-context this))))

(with-app-with-cli-data
  app
  [ck/registry router test-service]
  {:config "./dev-resources/test-config.conf"}
  (let [serv (app/get-service app :ResultService)
        [route action] (first (get-result serv))]
    (fact route => "/hello/world")
    (fact (ckp/invoke action {:foo "bar"} {}) => {:hello "world" :req {:foo "bar"}})))