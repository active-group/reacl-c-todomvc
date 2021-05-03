(ns todomvc.history
  (:require [reacl-c.core :as c :include-macros true]
            [active.clojure.lens :as lens]))

(c/defn-effect push! [url]
  (.pushState js/window.-history nil nil url))

(c/defn-subscription listen deliver! []
  (let [h (fn [ev]
            (deliver! js/location))]
    (js/window.addEventListener "popstate" h)
    (deliver! js/location)
    (fn []
      (js/window.removeEventListener "popstate" h))))

(c/defn-item with-current-hash [f]
  (c/with-state-as [_ hash :local ""]
    (c/fragment (c/focus lens/second
                         (-> (listen)
                             (c/handle-action (fn [_ location]
                                                (.-hash location)))))
                (c/focus lens/first (f hash)))))
