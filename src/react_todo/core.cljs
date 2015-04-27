(ns ^:figwheel-always react-todo.core
  (:require [cljs-uuid-utils.core :as uuid]
            [reagent.core :as ra]))

(enable-console-print!)

(println "welcome! 吃饭了吗?")

(defn uuid-str []
  (uuid/uuid-string (uuid/make-random-uuid)))

(defn content-with-id [content]
  { :id (uuid-str)
    :content content })

;; define your app data so that it doesn't get over-written on reload
(defonce app-state
  (atom {:items (map content-with-id ["practice 普通话" "inbox zero"])}))

(defn add-todo [todo]
  (swap! app-state dissoc :form-field)
  (swap! app-state (fn [data]
    (update-in data [:items] (fn [items]
      (conj items (content-with-id todo)))))))

(defn delete-todo [todo]
  (swap! app-state
    (fn [data] (update-in data [:items]
      (fn [items]
        (remove #(= (:id %) (:id todo)) items))))))

(defn input-change-handler [event]
  (.preventDefault event)
  (let [value (.. event -target -value)]
    (swap! app-state
      assoc-in [:form-field] value)))

(defn submit-todo-handler [event]
  (.preventDefault event)
  (let [new-todo (:form-field @app-state)]
    (add-todo new-todo)))

(defn delete-todo-handler [todo event]
  (.preventDefault event)
  (delete-todo todo))

(defn todo-item [todo]
  [:li {:key (:id todo)} (:content todo) " "
    [:a {:href "#"
         :onClick (partial delete-todo-handler todo)} "delete"]])

(defn todo-list [items]
  [:ul (map todo-item items)])

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
