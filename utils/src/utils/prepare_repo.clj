(ns utils.prepare-repo
  (:require [leiningen.core.project :as lein-project])
  (:require [cemerick.pomegranate.aether :as aether])

  (:require [clj-common.path :as path])
  (:require [clj-common.logging :as logging])
  (:require [clj-common.jar :as jar])
  (:require [utils.env :as env])
  (:use clj-common.clojure))

(defn clj-scraper-repo []
  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))
        destination-path ["tmp" "jar-extract"]]
    (doseq [jar-path (map
                       #(path/string->path (.getPath %1))
                       (aether/dependency-files
                         (aether/resolve-dependencies
                           :coordinates (:dependencies scraper-project))))]
      (let [jar-path-s (path/path->string jar-path)]
        (if (.contains jar-path-s "com/mungolab/clj-common")
          (logging/report "skipping clj-common")
          (jar/extract jar-path env/repo-clj-scraper))))))


(defn clojure-repo []
  ; todo
  ; for this I need support for reading poms and preparing repositories
  ; since clojure depends only on two spec jars I extracted them manually and prepared repo

  (todo "done manually for now"))

(comment
  (clj-scraper-repo)


  ; didn't work
  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (run!
      logging/report
      (filter
        #(not (= (take 3 %1) ["com" "mungolab" "clj-common"]))
        (map #(path/string->path (.getPath %1))
             (aether/dependency-files
               (aether/resolve-dependencies :coordinates (:dependencies scraper-project)))))))



  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (run!
      logging/report
      (mapcat
        (fn [jar-path]
          (jar/contents jar-path))
        (map #(path/string->path (.getPath %1))
             (aether/dependency-files
               (aether/resolve-dependencies :coordinates (:dependencies scraper-project)))))))


  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (logging/report
      (into
        #{}
        (mapcat
          (fn [jar-path]
            (map
              (partial take 1)
              (jar/contents jar-path)))
          (map #(path/string->path (.getPath %1))
               (aether/dependency-files
                 (aether/resolve-dependencies :coordinates (:dependencies scraper-project))))))))




  ; lists all dependencies as jars ...
  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (run!
      logging/report
      (aether/dependency-files
        (aether/resolve-dependencies :coordinates (:dependencies scraper-project)))))


  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (run!
      logging/report
      (aether/resolve-dependencies :coordinates (:dependencies scraper-project))))

  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (run!
      logging/report
      (:dependencies scraper-project)))

  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (logging/report (keys (aether/resolve-dependencies :coordinates (:dependencies scraper-project)))))


  (logging/report "test")
  (let [scraper-project (lein-project/read
                          (path/path->string (path/child env/clj-scraper-home "project.clj")))]
    (logging/report (:dependencies scraper-project)))



  (aether/resolve-dependencies :coordinates [['com.mungolab/clj-common "0.1.0-SNAPSHOT"]])

  (lein-project/read "/Users/vanja/projects/clj-common/project.clj")
)
