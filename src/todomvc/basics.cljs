(ns todomvc.basics
  (:require [reacl-c.core :as c :include-macros true]
            [active.clojure.lens :as lens]
            [reacl-c.dom :as dom :include-macros true]))

(dom/defn-dom checkbox [attrs & content]
  (c/with-state-as value
    (apply dom/input (dom/merge-attributes {:type "checkbox"
                                            :checked value
                                            :onChange (fn [_ ev]
                                                        (.-checked (.-target ev)))}
                                           attrs)
           content)))

(dom/defn-dom input-string [attrs & content]
  (c/with-state-as text
    (apply dom/input (dom/merge-attributes
                      {:type "text"
                       :value text
                       :onChange
                       (fn [_ ev]
                         (.-value (.-target ev)))}
                      attrs)
           content)))

(dom/defn-dom edit-text [attrs & content]
  (apply input-string
         (dom/merge-attributes
          {:autoFocus true
           :onKeyDown
           (fn [text ev]
             (case (.-keyCode ev)
               27 (do (.preventDefault ev)
                      (c/call (:onCancel attrs)))
               13 (do (.preventDefault ev)
                      (c/call (:onCommit attrs)))
               (c/return)))}
          (dissoc attrs :onCommit :default))
         content))

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
