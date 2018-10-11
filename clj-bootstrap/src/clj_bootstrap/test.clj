(require 'clj-common.jar)
(require 'clj-common.jvm)
(require 'clj-common.logging)

(run!
  println
  (filter
    (fn [path]
      (let [classes (clj-common.jar/contents path)]
        (some? (first
                 (filter
                   #(.contains (last %) "CPool")
                   classes)))))
    (filter
      #(.endsWith (last %) ".jar")
      (clj-common.jvm/classpath-as-path-seq))))


(run!
  println
  (clj-common.jvm/classpath-as-path-seq))



(println "not jars")
(run!
  println
  (filter
    #(not (.endsWith (last %) ".jar"))
    (clj-common.jvm/classpath-as-path-seq)))



(run!
  println
  (clj-common.jar/contents
    (first
      (filter
        #(.endsWith (last %) ".jar")
        (clj-common.jvm/classpath-as-path-seq)))))
