(ns todomvc.basics
  (:require [reacl-c.core :as c :include-macros true]
            [active.clojure.lens :as lens]
            [reacl-c.dom :as dom :include-macros true]
            [reacl-c-basics.forms.core :as forms]))

(dom/defn-dom edit-text [attrs]
  (forms/input
   (dom/merge-attributes
    {:autoFocus true
     :type "text"
     :onKeyDown
     (fn [text ev]
       (case (.-keyCode ev)
         27 (do (.preventDefault ev)
                (c/return :action (c/call-handler (:onCancel attrs))))
         13 (do (.preventDefault ev)
                (c/return :action (c/call-handler (:onCommit attrs))))
         (c/return)))}
    (dissoc attrs :onCommit :default))))

(dom/defn-dom text-form [attrs]
  (c/isolate-state
   ""
   (edit-text (-> attrs
                  (assoc :onCancel (constantly "")
                         :onCommit (fn [text]
                                     (if-let [text (not-empty (.trim text))]
                                       (c/merge-returned (c/call (:onCommit attrs) text)
                                                         (c/return :state ""))
                                       (c/return :state ""))))))))
