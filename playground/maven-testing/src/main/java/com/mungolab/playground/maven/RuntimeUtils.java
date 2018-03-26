package com.mungolab.playground.maven;

import java.lang.reflect.Method;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
// migrated to com.mungolab.djvm.common.RuntimeUtils
public class RuntimeUtils {
    public static Method mainMethod(Class clazz) {
        try {
            Method mainMethod = clazz.getMethod("main", String[].class);
            return mainMethod;
        } catch (Exception e) {
            throw new RuntimeException("Unable to find main method", e);
        }
    }

    public static Method mainMethod(ClassLoader classLoader, String classS) {
        Class clazz;
        try {
            clazz = classLoader.loadClass(classS);
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract class", e);
        }

        return RuntimeUtils.mainMethod(clazz);
    }

    public static void runMainMethod(Class clazz) {
        try {
            Method mainMethod = RuntimeUtils.mainMethod(clazz);
            mainMethod.invoke(null, (Object) null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to run main method", e);
        }
    }

    public static void runMainMethod(ClassLoader classLoader, String classS) {
        try {
            Method mainMethod = RuntimeUtils.mainMethod(classLoader, classS);
            mainMethod.invoke(null, (Object) null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to run main method", e);
        }
    }
}
