(ns todomvc.list
  (:require [reacl-c.core :as c :include-macros true]
            [active.clojure.functions :as f]
            [todomvc.model :as model]
            [todomvc.item :refer [todo-item]]
            [reacl-c.dom :as dom :include-macros true]))

(dom/defn-dom todo-list :- model/Todos [attrs show-ids]
  (apply dom/ul (dom/merge-attributes {:class "todo-list"}
                                      (dissoc attrs :onDelete))
         (map (fn [id]
                (-> (c/focus (model/item id)
                             (todo-item {:onDelete (when-let [f (:onDelete attrs)]
                                                     (f/constantly (c/call f id)))}))
                    (c/keyed (str id))))
              show-ids)))
