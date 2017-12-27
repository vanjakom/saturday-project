package com.mungolab.dynamic.bootstrap.test;

import com.mungolab.dynamic.core.ClojureUtils;
import com.mungolab.dynamic.core.DynamicClassLoader;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class ClojureMultipleReload {
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
        repositories.add("/Users/vanja/projects/saturday-project/repos/fasterxml");

        DynamicClassLoader classLoader = new DynamicClassLoader(repositories);


//        Class clojureClass = classLoader.loadClass("clojure.java.api.Clojure");
//        Class iFnClass = classLoader.loadClass("clojure.lang.IFn");
//
//        Method clojureVarMethod = clojureClass.getMethod("var", Object.class, Object.class);
//        Method clojureReadMethod = clojureClass.getMethod("read", String.class);
//
//        Object requireFn = clojureVarMethod.invoke(null, new String[] { "clojure.core", "require" });
//        Method iFnInvokeMethod = iFnClass.getMethod("invoke", Object.class);
//        Method iFnInvokeZeroMethod = iFnClass.getMethod("invoke", null);
//
//        Object namespaceObject = clojureReadMethod.invoke(null, namespace);
//        iFnInvokeMethod.invoke(requireFn, namespaceObject);
//
//        Object functionObject = clojureVarMethod.invoke(null, new String[] { namespace, function });
//
//        System.out.println("Starting app");
//
//        iFnInvokeZeroMethod.invoke(functionObject, null);

        System.out.println("Preparing Clojure");
        ClojureUtils.requireNamespace(classLoader, namespace);

        System.out.println("Running application");
        ClojureUtils.invokeFunctionArity0(classLoader, namespace, function);


        System.out.println("Preparing Clojure once more");
        ClojureUtils.requireNamespace(classLoader, namespace);

        System.out.println("Running application once more");
        ClojureUtils.invokeFunctionArity0(classLoader, namespace, function);

        
        System.out.println("Preparing Clojure once more with reloading");
        ClojureUtils.requireWithReloadNamespace(classLoader, namespace);

        System.out.println("Running application once more");
        ClojureUtils.invokeFunctionArity0(classLoader, namespace, function);
    }
}
