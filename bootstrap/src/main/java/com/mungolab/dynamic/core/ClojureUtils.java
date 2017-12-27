package com.mungolab.dynamic.core;

import java.lang.reflect.Method;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class ClojureUtils {
    public static void requireNamespace(
            ClassLoader classLoader,
            String namespace) throws RuntimeException {

        try {
            Class clojureClass = classLoader.loadClass("clojure.java.api.Clojure");
            Class iFnClass = classLoader.loadClass("clojure.lang.IFn");
            Method clojureVarMethod = clojureClass.getMethod("var", Object.class, Object.class);
            Method clojureReadMethod = clojureClass.getMethod("read", String.class);

            Object requireFn = clojureVarMethod.invoke(null, new String[] { "clojure.core", "require" });
            Method iFnInvokeMethod = iFnClass.getMethod("invoke", Object.class);

            Object namespaceObject = clojureReadMethod.invoke(null, namespace);
            iFnInvokeMethod.invoke(requireFn, namespaceObject);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load namespace: " + namespace, e);
        }
    }

    public static void requireWithReloadNamespace(
            ClassLoader classLoader,
            String namespace) throws RuntimeException {

        try {
            Class clojureClass = classLoader.loadClass("clojure.java.api.Clojure");
            Class iFnClass = classLoader.loadClass("clojure.lang.IFn");
            Method clojureVarMethod = clojureClass.getMethod("var", Object.class, Object.class);
            Method clojureReadMethod = clojureClass.getMethod("read", String.class);

            Object requireFn = clojureVarMethod.invoke(null, new String[] { "clojure.core", "require" });
            Method iFnInvoke1Method = iFnClass.getMethod("invoke", Object.class);
            Method iFnInvoke2Method = iFnClass.getMethod("invoke", Object.class, Object.class);

            Object namespaceObject = clojureReadMethod.invoke(null, namespace);

            Object clojureKeywordFn = clojureVarMethod.invoke(null, new String[] { "clojure.core", "keyword" });
            Object reloadAllKeywordObject = iFnInvoke1Method.invoke(clojureKeywordFn, "reload-all");

            iFnInvoke2Method.invoke(requireFn, namespaceObject, reloadAllKeywordObject);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load namespace: " + namespace, e);
        }
    }

    public static void invokeFunctionArity0(
            ClassLoader classLoader,
            String namespace,
            String function) throws RuntimeException {

        try {
            Class clojureClass = classLoader.loadClass("clojure.java.api.Clojure");
            Class iFnClass = classLoader.loadClass("clojure.lang.IFn");
            Method clojureVarMethod = clojureClass.getMethod("var", Object.class, Object.class);
            Method iFnInvokeZeroMethod = iFnClass.getMethod("invoke", null);

            Object functionObject = clojureVarMethod.invoke(null, new String[]{namespace, function});
            iFnInvokeZeroMethod.invoke(functionObject, null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke fn: " + function + " in namespace: " + namespace, e);
        }
    }

}
