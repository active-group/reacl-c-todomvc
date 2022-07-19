(ns ^:dev/always todomvc.main
  (:require [reacl-c.main :as main]
            [todomvc.core :as core]
            [todomvc.model :as model]
            [todomvc.history :as history]
            [todomvc.storage :as storage]))

(def app
  (->> core/todo-app
       (history/with-current-hash)
       (storage/with-todos-storage model/empty-todos)))

(main/run (first (js/document.getElementsByClassName "todoapp"))
  app)
