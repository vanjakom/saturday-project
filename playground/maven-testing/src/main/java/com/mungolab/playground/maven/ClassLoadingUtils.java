package com.mungolab.playground.maven;


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
public class ClassLoadingUtils {
    public static final String PACKAGE = "package";
    public static final String NAME = "name";

    public static class ClassDesc extends LanguageUtils.MapProxy<String, String> implements Map<String, String> {
        public ClassDesc(String packageS, String nameS) {
            super(LanguageUtils.mapOf(
                    PACKAGE, packageS, 
                    NAME, nameS));
        }

        public String getPackage() {
            return this.get(PACKAGE);
        }

        public String getName() {
            return this.get(NAME);
        }
    }

    public static String classDescToString(ClassDesc classDesc) {
        return classDesc.getPackage() + "." + classDesc.getName();
    }

    public static ClassDesc jarNameToClassDesc(String jarName) {
        String[] parts = jarName.split("/");

        String classPart = parts[parts.length - 1];
        List<String> packageParts = LanguageUtils.dropLast(Arrays.asList(parts));

        // remove .class from classes
        return new ClassDesc(String.join(".", packageParts),  classPart.substring(0, classPart.length() - 6));
    }

    public static List<ClassDesc> extractClasses(String path) {
        try {
            JarFile jarFile = new JarFile(new File(path));

            return LanguageUtils.enumerationAsStream(jarFile.entries())
                    .map(entry -> { return entry.getName(); })
                    .filter(ClassLoadingUtils::filterClasses)
                    .map(ClassLoadingUtils::jarNameToClassDesc)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract classes from jar: " + path, e);
        }
    }

    public static String rootPackage(List<ClassDesc> classes) {
        return LanguageUtils.reduce(
                classes.stream(),
                (state, entry) -> {
                    if (state == null) {
                        return entry.getPackage();
                    } else if (state.length() < entry.getPackage().length()) {
                        return state;
                    } else {
                        return entry.getPackage();
                    }
                },
                null);
    }

    public static boolean filterClasses(String jarName) {
        return jarName.endsWith(".class");
    }

    public static class JarClassLoader extends ClassLoader {
        private final ClassLoader parent;
        private final ClassLoader proxy;

        public JarClassLoader(ClassLoader parent, File jarFile) {
            this.parent = parent;

            try {
                this.proxy = new URLClassLoader(new URL[] { new URL(jarFile.getAbsolutePath()) });
            } catch (Exception e) {
                throw new RuntimeException("Unable to setup ClassLoader", e);
            }
        }

    }

    public static void main(String[] args) throws Exception {
        String path = "/Users/vanja/projects/saturday-project/playground/maven-testing/target/local-repo/org/eclipse/jetty/jetty-server/9.4.8.v20171121/jetty-server-9.4.8.v20171121.jar";

        List<ClassDesc> classes = extractClasses(path);


        classes
                .stream()
                .forEach(classDesc -> {
                    System.out.println(classDescToString(classDesc));});


        System.out.println("Package: " + rootPackage(classes));

    }
}
