(ns todomvc.model
  (:require [schema.core :as s :include-macros true]
            [active.clojure.lens :as lens]))

(s/defschema TodoItem
  {:completed s/Bool
   :title s/Str})

(def completed :completed)
(def title :title)

(s/defschema Todos
  {:ids [s/Uuid]
   :items {s/Uuid TodoItem}})

(def empty-todos {:ids [] :items {}})

(defn item-count [v] (count (:items v)))

(defn item [id]
  (lens/>> :items (lens/member id)))

(defn add-todo
  [todos text]
  (let [id (inc (apply max 0 (:ids todos)))]
    (-> todos
        (update :ids conj id)
        (update :items assoc id {:completed false :title text}))))

(defn delete-todo [todos id]
  (-> todos
      (update :items dissoc id)
      (update :ids #(remove #{id} %))))

(defn completed-ids [todos]
  (->> (:items todos)
       (filter (fn [[id v]]
                 (:completed v)))
       (map first)))

(defn clear-completed [todos]
  (reduce delete-todo
          todos
          (completed-ids todos)))

(defn mark-all
  ([todos] (every? :completed (vals (:items todos))))
  ([todos mark?]
   (update todos :items
           (fn [items] (into {} (mapv (fn [[id it]]
                                        [id (assoc it :completed mark?)])
                                      items))))))

(s/defschema Filter (s/enum :all :active :completed))

(defn apply-filter [todos filt]
  (let [all-ids (:ids todos)
        items (:items todos)]
    (case filt
      :all all-ids
      :active (remove (set (completed-ids todos))  all-ids)
      :completed (filter (set (completed-ids todos))  all-ids))))

(defn parse-todos [dflt v]
  (if (empty? v)
    dflt
    (let [l (js->clj (js/JSON.parse v) :keywordize-keys true)]
      {:ids (map :id l)
       :items (into {} (map (fn [v]
                              [(:id v)
                               (dissoc v :id)])
                            l))})))

(defn unparse-todos [todos]
  (js/JSON.stringify (clj->js (mapv (fn [id]
                                      (-> (get (:items todos) id)
                                          (assoc :id id)))
                                    (:ids todos)))))

