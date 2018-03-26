package com.mungolab.djvm.common;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class ClassLoaderUtils {
    public static final String CLASS_DESC_PACKAGE = "package";
    public static final String CLASS_DESC_NAME = "name";

    public static class JarClassLoader extends URLClassLoader {
        public JarClassLoader(ClassLoader parent, File jarFile) throws Exception {
            super(new URL[]{ new URL("file://" + jarFile.getAbsolutePath()) }, parent);
        }
    }

    public static List<Map<String, String>> extractClasses(String path) {
        try {
            JarFile jarFile = new JarFile(new File(path));

            return LanguageUtils.enumerationAsStream(jarFile.entries())
                    .map(entry -> { return entry.getName(); })
                    .filter(entry -> {
                        return entry.endsWith(".class"); })
                    .map(entry -> {
                        String[] parts = entry.split("/");

                        String classPart = parts[parts.length - 1];
                        List<String> packageParts = LanguageUtils.dropLast(Arrays.asList(parts));

                        // remove .class from classes
                        return LanguageUtils.mapOf(
                                CLASS_DESC_PACKAGE, String.join(".", packageParts),
                                CLASS_DESC_NAME, classPart.substring(0, classPart.length() - 6)); })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract classes from jar: " + path, e);
        }
    }
}
