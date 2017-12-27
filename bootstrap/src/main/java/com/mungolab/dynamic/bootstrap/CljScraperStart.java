package com.mungolab.dynamic.bootstrap;

import com.mungolab.dynamic.core.ClojureUtils;
import com.mungolab.dynamic.core.DynamicClassLoader;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class CljScraperStart {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting clj-scrapper");

        List<String> repositories = new LinkedList<String>();
        repositories.add("/Users/vanja/projects/clj-scraper/src/");
        // configuration.edn is in resources
        repositories.add("/Users/vanja/projects/clj-scraper/resources/");
        repositories.add("/Users/vanja/projects/clj-common/src/");
        // clojure must be in front of rest of prepared repo
        repositories.add("/Users/vanja/open-source/clojure/target/classes");
        repositories.add("/Users/vanja/projects/saturday-project/repos/clojure-dependencies");
        // clj-common will be removed from repo once created
        repositories.add("/Users/vanja/projects/saturday-project/repos/clj-scraper");

        DynamicClassLoader classLoader = new DynamicClassLoader(repositories);

        // todo
        // trying to fix problem with loading resource files and unable to find which loader to user
        // clj-scraper.server/status-handler using
        // clj-common.jvm/resource-as-stream
        // working, interesting is that Jetty is transferring class loader from main thread to request threads

        Thread.currentThread().setContextClassLoader(classLoader);


        System.out.println("Preparing Clojure");
        ClojureUtils.requireNamespace(classLoader, "clj-scraper.main");

        System.out.println("Running application");
        ClojureUtils.invokeFunctionArity0(classLoader, "clj-scraper.main", "start");
    }
}
