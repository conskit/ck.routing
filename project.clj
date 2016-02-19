(defproject ck.routing "0.1.0-SNAPSHOT"
  :description "Routing module for Conskit"
  :url "https://github.com/conskit/ck.routing"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [conskit "0.1.0-SNAPSHOT"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[puppetlabs/trapperkeeper "1.2.0" :classifier "test"]
                                  [midje "1.8.3"]]
                   :plugins [[lein-midje "3.2"]]}})
