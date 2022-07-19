(ns todomvc.item
  (:require [reacl-c.core :as c :include-macros true]
            [active.clojure.functions :as f]
            [active.clojure.lens :as lens]
            [reacl-c-basics.forms.core :as forms]
            [todomvc.basics :as b]
            [todomvc.model :as model]
            [reacl-c.dom :as dom :include-macros true]))

(dom/defn-dom todo-item-text-editor [attrs]
  (c/with-state-as [todo edit]
    (when (some? edit)
      (c/focus lens/second
               (b/edit-text (dom/merge-attributes
                             {:class "edit"
                              :onCancel (f/constantly nil)}
                             attrs))))))

(defn commit-text [onDelete [todo new-text]]
  (if-let [t (not-empty (.trim new-text))]
    [(assoc todo model/title t)
     nil]
    (c/call onDelete)))

(dom/defn-dom todo-item [attrs]
  (c/with-state-as [todo edit :local nil]
    (dom/li (dom/merge-attributes
             {:class (cond-> ""
                       (model/completed todo) (str " completed")
                       (some? edit) (str " editing"))}
             (dissoc attrs :onDelete))
            (dom/div {:class "view"}
                     (c/focus (lens/>> lens/first model/completed) (forms/input {:type "checkbox" :class "toggle"}))
                     (c/focus lens/second
                              (dom/label {:onDoubleClick
                                          ;; TODO: focus?
                                          (f/constantly (model/title todo))}
                                         (model/title todo)))
                     (dom/button {:class "destroy"
                                  :onClick (:onDelete attrs)}))
            (todo-item-text-editor {:onCommit (f/partial commit-text (:onDelete attrs))
                                    :onBlur (f/partial commit-text (:onDelete attrs))}))))
