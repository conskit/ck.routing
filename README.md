# ck.routing [![Build Status](https://travis-ci.org/conskit/ck.routing.svg?branch=master)](https://travis-ci.org/conskit/ck.routing) [![Dependencies Status](https://jarkeeper.com/conskit/ck.routing/status.svg)](https://jarkeeper.com/conskit/ck.routing) [![Clojars Project](https://img.shields.io/clojars/v/ck.routing.svg)](https://clojars.org/ck.routing)

Routing module for [conskit](https://github.com/conskit/conskit)
## Installation
Add the dependency in the clojars badge above in your `project.clj`.

### bidi support
Add the classifier "bidi" and the `bidi` library.

## Usage

Add the following to your `bootstrap.cfg`:

```
ck.routing/router
```

When the router starts up it looks for all actions that specify a `:route` annotation and creates a vector of maps with the id and route value i.e.

```clojure
(ns foo-app)
...
(action
  ^{:route "/"}
  action1
  [req]
  ;; logic
  )
(action
  ^{:route "/page"}
  action2
  [req]
  ;; logic
  )
  
;; Produces
[{:id :foo-app/action1 :route "/"} {:id :foo-app/action2 :route "/page"}]
```

To access this vector of routes simply call the `get-routes` method by adding the dependency in your serivice.

You can also create a [ring handler](https://github.com/ring-clojure/ring/wiki/Concepts#handlers) using the `make-ring-hanlder` method

```clojure
(defservice
  my-service
  [[:CKRouter get-routes make-ring-hanlder]]
  (init [this context]
    ...
    (some-ring-based-web-server 
      (make-ring-handler :bidi))
  ...)
```

`make-ring-handler` is called with a provider (`:bidi`).

### Alternatives
If Bidi is not your cup of tea you can always implement your own provider by providing a method that extends the `ck.routing/make-ring-handler*` multimethod

```clojure
(defmethod make-ring-handler* :my-special-provider
  [{:keys [routes get-action]}]
  ;; logic
  )
  
;; Within service
(make-ring-hanlder :my-special-provide)
```

where `routes` is the same vector created when the router is started and `get-action` is the method provided by the `:ActionRegistry` that can retrieve an action instance from its id. The action instance can then be called via `conskit.protocols/invoke` (see `bidi/ck/routing/bidi.clj` for an example).

## License

Copyright © 2016 Jason Murphy

Distributed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
