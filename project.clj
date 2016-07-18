(defproject ck.routing "0.1.0-SNAPSHOT"
  :description "Routing module for Conskit"
  :url "https://github.com/conskit/ck.routing"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [conskit "0.2.0-SNAPSHOT"]]
  :profiles {:dev {:source-paths ["dev" "bidi"]
                   :dependencies [[puppetlabs/trapperkeeper "1.4.1" :classifier "test"]
                                  [puppetlabs/kitchensink "1.3.1" :classifier "test" :scope "test"]
                                  [midje "1.8.3"]
                                  [bidi "1.25.1"]]
                   :plugins [[lein-midje "3.2"]]}
             :bidi-routing {:source-paths ["bidi"]
                            :dependencies [[bidi "1.25.1"]]}}
  :classifiers {:bidi :bidi-routing})
