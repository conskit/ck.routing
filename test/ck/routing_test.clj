(ns ck.routing-test
  (:require
    [puppetlabs.trapperkeeper.app :as app]
    [puppetlabs.trapperkeeper.core :refer [defservice]]
    [puppetlabs.trapperkeeper.services :refer [service-context]]
    [puppetlabs.trapperkeeper.testutils.bootstrap :refer [with-app-with-cli-data]]
    [ck.routing :refer [make-ring-handler* router]]
    [conskit.core :as ck]
    [conskit.macros :refer :all]
    [ck.routing.bidi])
  (:use midje.sweet))

(defmethod make-ring-handler* :test
  [{:keys [routes get-action]}]
  (for [r routes
        :let [{:keys [id route]} r]]
    [route (get-action id)]))

(fact (make-ring-handler* {:provider :test
                           :routes   [{:id :my-action :route "/foo/bar"}]
                           :get-action #(identity %)}) => [["/foo/bar" :my-action]])


(defcontroller
  my-controller
  []
  (action
    ^{:route ["/hello/world/" [#".*" :id]]}
    my-action
    [req]
    {:hello "world" :req req})
  (action
    ^{:route #".*?\/foo.*"}
    route-action
    [req]
    {:hello "world" :req req})
  (action
    ^{:route true}
    catch-all-action
    [req]
    {:hello "not found"})
  (action
    ^{:route {:request-method :get
              :ck-route ["/article/" :id ]}}
    get-complex-route-action
    [req]
    {:hello "world" :req req})
  (action
    ^{:route {:request-method :post
              :ck-route ["/article/" :id ]}}
    post-complex-route-action
    [req]
    {:hello "world"}))

(defprotocol ResultService
  (get-result [this]))

(defservice
  test-service ResultService
  [[:ActionRegistry register-controllers!]
   [:CKRouter make-ring-handler get-routes]]
  (init [this context]
        (register-controllers! [my-controller])
        context)
  (start [this context]
         {:result (make-ring-handler :bidi)})
  (get-result [this]
              (:result (service-context this))))

(with-app-with-cli-data
  app
  [ck/registry router test-service]
  {:config "./dev-resources/test-config.conf"}
  (let [serv (app/get-service app :ResultService)
        handler (get-result serv)]
    (fact (handler {:uri "/idontknow"}) =>
          {:hello "not found"})
    (fact (handler {:uri "/article/1" :request-method :get}) =>
          {:hello "world", :req {:uri "/article/1", :request-method :get, :params {:id "1"}, :route-params {:id "1"}}})
    (fact (handler {:uri "/hello/world/26/37"}) =>
          {:hello "world", :req {:params {:id "26/37"}, :route-params {:id "26/37"}, :uri "/hello/world/26/37"}})
    (fact (handler {:uri "/foo/bar/baz/qux/quux/corge/grault/garply"}) =>
          {:hello "world", :req {:params nil, :route-params nil, :uri "/foo/bar/baz/qux/quux/corge/grault/garply"}})))