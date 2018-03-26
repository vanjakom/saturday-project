package com.mungolab.djvm.common;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class RuntimeUtils {
    static List<Supplier<Boolean>> tests = new LinkedList<>();

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

    public static void invokeMainMethod(Class clazz) {
        try {
            Method mainMethod = RuntimeUtils.mainMethod(clazz);
            mainMethod.invoke(null, (Object) null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to run main method", e);
        }
    }

    public static void invokeMainMethod(ClassLoader classLoader, String classS) {
        try {
            Method mainMethod = RuntimeUtils.mainMethod(classLoader, classS);
            mainMethod.invoke(null, (Object) null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to run main method", e);
        }
    }

    public static <T> T invokeStaticMethod(
            ClassLoader classLoader,
            String packageS,
            String classS,
            Class<? extends T> returnType,
            String methodName,
            Class[] argClasses,
            Object[] args) {

        try {
            Class clazz = classLoader.loadClass(packageS + "." + classS);

            Method method = clazz.getMethod(methodName, argClasses);

            return (T)method.invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke method: " + methodName + " of " + packageS + "." + classS, e);
        }
    }

    // note: does not support static methods with generics
    public static <T> T invokeStaticMethod(
            ClassLoader classLoader,
            String packageS,
            String classS,
            Class<? extends T> returnType,
            String methodName,
            Object... args) {

        Class[] argClasses = Arrays.stream(args).map(arg -> {
            return arg.getClass();
        }).toArray(Class[]::new);

        return invokeStaticMethod(classLoader, packageS, classS, returnType, methodName, argClasses, args);
    }

    static {
        tests.add(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                Map<String, String> map = RuntimeUtils.invokeStaticMethod(
                        RuntimeUtils.class.getClassLoader(),
                        "com.mungolab.djvm.common",
                        "LanguageUtils",
                        Map.class,
                        "mapOf",
                        LanguageUtils.arrayOf(Object.class, Object.class, Object.class, Object.class),
                        LanguageUtils.arrayOf("key1", "value1", "key2", "value2"));


                return
                        map.get("key1").equals("value1") &&
                        map.get("key2").equals("value2") &&
                        map.get("key3") == null;
            }
        });
    }

    // execute all tests
    static {
        tests.forEach(test -> {
            if (!test.get()) {
                throw new RuntimeException("LanguageUtils: Test failed");
            }
        });
    }
    
    public static void main(String[] args) {
        System.out.println("Just to invoke tests");
    }
}
