(ns ck.routing.bidi
  (:require [ck.routing :refer [make-ring-handler*]]
            [bidi.ring :as br]))

(defmethod make-ring-handler* :bidi
  [{:keys [routes get-action]}]
  (br/make-handler routes))
