(ns todomvc.core
  (:require [reacl-c.core :as c :include-macros true]
            [reacl-c-basics.forms.core :as forms]
            [active.clojure.functions :as f]
            [active.clojure.lens :as lens]
            [todomvc.basics :as b]
            [todomvc.model :as model]
            [todomvc.list :refer [todo-list]]
            [reacl-c.dom :as dom :include-macros true]))

(c/def-item header :- model/Todos
  (dom/header {:class "header"}
              (dom/h1 "todos")
              (b/text-form {:class "new-todo"
                            :placeholder "What needs to be done???"
                            :onCommit model/add-todo})))

(c/defn-item main :- model/Todos [filter :- model/Filter]
  (c/with-state-as todos
    (dom/section {:class "main"}
                 (c/focus model/mark-all
                          (c/fragment
                           (forms/input {:type "checkbox"
                                         :class "toggle-all"
                                         :id "toggle-all"})
                           (dom/label {:for "toggle-all"})))
                 (todo-list {:onDelete model/delete-todo}
                            (model/apply-filter todos filter)))))

(c/defn-item counter :static [cnt]
  (dom/span {:class "todo-count"}
            (dom/strong (str cnt))
            (case cnt
              (case cnt
                0 " items left"
                1 " item left"
                " items left"))))

(def filter-hash
  {:all "#/"
   :active "#/active"
   :completed "#/completed"})

(defn filter-from-hash [hash]
  (case hash
    "#/active" :active
    "#/completed" :completed
    :all))

(c/defn-item filters [active]
  (dom/ul {:class "filters"}
          (let [a (fn [filter label]
                    (dom/a {:class (when (= active filter) "selected") :href (filter-hash filter)} label))]
           (dom/li (a :all "All")
                   (a :active "Active")
                   (a :completed "Completed")))))

(c/def-item clear-completed-button :- model/Todos
  (c/with-state-as todos
    (when-not (empty? (model/completed-ids todos))
      (dom/button {:class "clear-completed"
                   :onClick model/clear-completed}
                  "Clear completed"))))

(c/defn-item footer :- model/Todos [filter :- model/Filter]
  (dom/footer {:class "footer"}
              (c/dynamic (comp counter model/item-count))
              (filters filter)
              clear-completed-button))

(c/defn-item todo-app :- model/Todos [filter-hash]
  (c/with-state-as todos
    (let [filter (filter-from-hash filter-hash)]
      (c/fragment header
                  (when-not (zero? (model/item-count todos))
                    (c/fragment (main filter)
                                (footer filter)))))))
