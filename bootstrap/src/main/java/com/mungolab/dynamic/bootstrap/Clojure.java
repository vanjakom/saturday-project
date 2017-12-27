package com.mungolab.dynamic.bootstrap;

import com.mungolab.dynamic.core.ClojureUtils;
import com.mungolab.dynamic.core.DynamicClassLoader;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Clojure {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting bootstrap");

        String functionInNamespace = args[0];
        String namespace = functionInNamespace.split("/")[0];
        String function = functionInNamespace.split("/")[1];
        System.out.println("Namespace:" + namespace);
        System.out.println("Function:" + function);

        List<String> repositories = new LinkedList<String>();
        repositories.add("/Users/vanja/projects/saturday-project/apps/sample-clj-raw");
        repositories.add("/Users/vanja/open-source/clojure/target/classes");
        repositories.add("/Users/vanja/projects/saturday-project/repos/clojure-dependencies");


        DynamicClassLoader classLoader = new DynamicClassLoader(repositories);


        System.out.println("Preparing Clojure");
        ClojureUtils.requireNamespace(classLoader, namespace);

        System.out.println("Running application");
        ClojureUtils.invokeFunctionArity0(classLoader, namespace, function);
    }
}
