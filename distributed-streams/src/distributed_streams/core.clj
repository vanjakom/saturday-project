(ns distributed-streams.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(require '[clj-common.time :as time])
(require '[clj-common.logging :as logging])

(time/timestamp-second)

(require '[clojure.core.async :as async])



(def channel (async/chan))

(async/go-loop []
  (logging/report "waiting for next message")
  (if-let [message (async/<! channel)]
    (do
      (logging/report {
                        :thread (.getName (Thread/currentThread))
                        :message message})
      (recur))
    (logging/report "exiting loop")))




(async/>!! channel "test")

(async/>!! channel "test 1")

(async/close! channel)
