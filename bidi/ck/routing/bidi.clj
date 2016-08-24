(ns ck.routing.bidi
  (:require [ck.routing :refer [make-ring-handler*]]
            [bidi.ring :as br]
            [conskit.protocols :as ckp]))

(defmethod make-ring-handler* :bidi
  [{:keys [routes get-action]}]
  (let [not-catch-all? #(not (true? (:route %)))
        catch-all (remove not-catch-all? routes)
        filtered-routes (filter not-catch-all? routes)
        rs (if (not-empty catch-all) (concat filtered-routes catch-all) routes)]
    (br/make-handler ["" (for [r rs
                               :let [{:keys [id route]} r]]
                           (if (map? route)
                             [(dissoc route :ck-route)
                              {(:ck-route route) #(ckp/invoke (get-action id) %)}]
                             [route #(ckp/invoke (get-action id) %)]))])))
