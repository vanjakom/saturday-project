package com.mungolab.dynamic.bootstrap;

import com.mungolab.dynamic.core.DynamicClassLoader;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting bootstrap");

        String mainClassS = args[0];
        System.out.println("Main class: " + mainClassS);

        List<String> repositories = new LinkedList<String>();
        repositories.add("/Users/vanja/projects/saturday-project/apps/sample-app/target/classes");
        repositories.add("/Users/vanja/projects/saturday-project/repos/fasterxml");

        DynamicClassLoader classLoader = new DynamicClassLoader(repositories);

        System.out.println("Starting app");
        Class mainClass = classLoader.loadClass(mainClassS);
        Method mainMethod = mainClass.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object)null);
    }
}
