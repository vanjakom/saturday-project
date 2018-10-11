
(require 'clojure.tools.nrepl.server)
;(require 'lighttable.nrepl.handler)

; https://github.com/clojure-emacs/cider-nrepl#via-embedding-nrepl-in-your-app
(require 'cider.nrepl)

(println "starting nrepl on 7000")

(clojure.tools.nrepl.server/start-server
  :handler (ns-resolve 'cider.nrepl 'cider-nrepl-handler)
  :port 7000)

(println "nrepl started")

