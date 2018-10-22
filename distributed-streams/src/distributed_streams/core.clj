(ns distributed-streams.core
  (:require
   [clojure.core.async :as async]
   [clj-common.localfs :as fs]
   [clj-common.io :as io]
   [clj-common.async :as async]
   [clj-common.jvm :as jvm]
   [clj-common.path :as path]))



(def channel (async/chan 1))

(async/go (async/>! channel "test"))
(async/<!! channel)


; utility for sequential writing to out
(def std-out-chan
  (let [channel (async/chan 1)]
    (async/go
      (loop
        (try
          (println "waiting")
          (println (async/<! std-out-chan))
          (if (not (async/closed?)))
          (catch Exception e (.printStackTrace e)))))))
(defn chan-println [& args]
  (async/>!! std-out-chan (clojure.string/join " " args)))

;;; todo
;;; when channel is closed go will enter indefinite while, check if channel is closed ...

(chan-println "test" "123")



(async/close! std-out-chan)




(.interrupt (Thread/currentThread))


(let [chan (async/chan 1 (map clojure.string/upper-case))]
  (doseq [index (range 3)]
    (async/go (async/>! chan (str "test " index))))
  (doseq [index (range 3)]
    (async/go (chan-println (jvm/thread-name) (async/<! chan)))))




(async/go-loop []
  (let [thread (.getName (Thread/currentThread))]
    (println thread "waiting for next message")
    (if-let [message (async/<! channel)]
      (do
        (println thread  message})
      (recur))
    (println "exiting loop")))


(println "test")

(async/>!! channel "test")

(async/>!! channel "test 1")

(async/close! channel)
