(defproject clj-bootstrap "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 ;[lein-light-nrepl "0.3.2"]
                 [cider/cider-nrepl "0.18.0"]

                 [com.mungolab/clj-common "0.2.0-SNAPSHOT"]
                 [com.mungolab/clj-aws "0.1.0-SNAPSHOT"]
		 [com.mungolab/clj-hadoop "0.1.0-SNAPSHOT"]
                 [com.mungolab.dynamic-jvm/common "1.0-SNAPSHOT"]
                 [com.mungolab.dynamic-jvm/bootstrap-maven "1.0-SNAPSHOT"]])
