(ns todomvc.storage
  (:require [reacl-c.core :as c :include-macros true]
            [todomvc.model :as model]))

(c/defn-effect local-storage-get [key]
  (js/window.localStorage.getItem key))

(c/defn-effect local-storage-set! [key value]
  (js/window.localStorage.setItem key value))

(defn with-todos-storage [initial item]
  (let [key "todos-reacl-c"]
    (c/isolate-state initial
                     (c/fragment
                      (c/execute-effect (local-storage-get key)
                                        model/parse-todos)
                      (-> item
                          (c/handle-state-change (fn [_ new]
                                                   (c/return :state new
                                                             :action (local-storage-set! key (model/unparse-todos new))))))))))
