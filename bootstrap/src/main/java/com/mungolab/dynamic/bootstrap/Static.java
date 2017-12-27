package com.mungolab.dynamic.bootstrap;

import com.mungolab.dynamic.core.DynamicClassLoader;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Static {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting bootstrap");

        String classS = args[0];
        String staticMethodS = args[1];
        System.out.println("Class: " + classS);
        System.out.println("Static method: " + staticMethodS);

        List<String> repositories = new LinkedList<String>();
        repositories.add("/Users/vanja/projects/saturday-project/apps/sample-app/target/classes");
        repositories.add("/Users/vanja/projects/saturday-project/apps/sample-clj-app/target/classes");
        repositories.add("/Users/vanja/open-source/clojure/target/classes");
        repositories.add("/Users/vanja/projects/saturday-project/repos/clojure-dependencies");
        repositories.add("/Users/vanja/projects/saturday-project/repos/fasterxml");

        DynamicClassLoader classLoader = new DynamicClassLoader(repositories);

        System.out.println("Starting app");
        Class clazz = classLoader.loadClass(classS);
        if (clazz != null) {
            Method staticMethod = clazz.getMethod(staticMethodS, null);
            staticMethod.invoke(null);
        } else {
            System.err.println("Unable to find class");
        }
    }
}
