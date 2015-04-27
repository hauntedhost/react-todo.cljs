(ns ^:figwheel-always react-todo.core
  (:require [reagent.core :as ra]))

(enable-console-print!)

(println "welcome! 吃饭了吗?")

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:items ["practice 普通话"
                                  "inbox zero"]}))

(defn add-todo [todo]
  (swap! app-state dissoc :form-field)
  (swap! app-state (fn [data]
    (update-in data [:items] (fn [items]
      (conj items todo))))))

(defn delete-todo [index]
  (swap! app-state
    (fn [data] (update-in data [:items]
      (fn [items]
        (reduce-kv (fn [a k v]
          (if (= k index) a (vec (conj a v)))) [] items))))))

(defn input-change-handler [event]
  (.preventDefault event)
  (let [value (.. event -target -value)]
    (swap! app-state
      assoc-in [:form-field] value)))

(defn submit-todo-handler [event]
  (.preventDefault event)
  (let [new-todo (:form-field @app-state)]
    (add-todo new-todo)))

(defn delete-todo-handler [index event]
  (.preventDefault event)
  (delete-todo index))

(defn todo-item [index todo]
  [:li {:key (str "todo_" index)} todo " "
    [:a {:key (str index)
         :href "#"
         :onClick (partial delete-todo-handler index)} "delete"]])

(defn todo-list [items]
  [:ul (map-indexed todo-item items)])

(defn todo-wrapper [data]
  [:div
    [:h1 "Todos"]
    [:form {:onSubmit submit-todo-handler}
      [:input {:name "todo"
               :value (:form-field data)
               :onChange input-change-handler}]]
    [todo-list (:items data)]])

(defn render [data]
  (ra/render [todo-wrapper data]
    (.getElementById js/document "app")))

(defonce watch-app-state
  (add-watch app-state :on-change
    (fn [_ _ _ new-state]
     (prn new-state)
     (render new-state))))

(render @app-state)
