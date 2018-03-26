package com.mungolab.djvm.common;

import java.util.Arrays;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class TestUtils {
    public static void runTests(String... classes) {
        // note:
        // ClassLoader classLoader = RunTests.class.getClassLoader();
        // classLoader.loadClass("com.mungolab.djvm.common.LanguageUtils")
        // is not initializing class and static blocks are executed in that stage
        // instead
        // Class.forName("com.mungolab.djvm.common.LanguageUtils")
        // was used

        Arrays.stream(classes).forEach(clazz -> {
            try {
                // failing test will cause Error
                System.out.println("Running: " + Class.forName(clazz).getName());
            } catch (Exception e) {
                throw new RuntimeException("Test loading failed: " + clazz, e);
            }
        });
    }
}
