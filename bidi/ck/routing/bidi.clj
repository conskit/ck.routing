(ns ck.routing.bidi
  (:require [ck.routing :refer [make-ring-handler*]]
            [bidi.ring :as br]
            [conskit.protocols :as ckp]))

(defmethod make-ring-handler* :bidi
  [{:keys [routes get-action]}]
  (br/make-handler ["" (for [r routes
                             :let [{:keys [id route]} r]]
                         (if (map? route)
                           [(dissoc route :ck-route)
                            {(:ck-route route) #(ckp/invoke (get-action id) %)}]
                           [route #(ckp/invoke (get-action id) %)]))]))
