(ns utils.env
  (:require [clj-common.path :as path]))

(def clj-scraper-home ["Users" "vanja" "projects" "clj-scraper"])

(def cojure-home ["Users" "vanja" "open-source" "clojure"])

(def saturday-project-home ["Users" "vanja" "projects" "saturday-project"])

(def saturday-project-repos (path/child saturday-project-home "repos"))

(def repo-clj-scraper (path/child
                        saturday-project-repos
                        "clj-scraper"))

(def repo-clojure (path/child
                    saturday-project-repos
                    "clojure-dependencies"))
