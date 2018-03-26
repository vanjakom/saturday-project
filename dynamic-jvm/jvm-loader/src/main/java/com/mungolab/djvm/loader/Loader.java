package com.mungolab.djvm.loader;

import com.mungolab.djvm.common.ClassLoaderUtils;
import com.mungolab.djvm.common.LanguageUtils;
import com.mungolab.djvm.common.PathUtils;
import com.mungolab.djvm.common.RuntimeUtils;

import java.util.Collection;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Loader {
    public static ClassLoader createMavenBootstrapClassLoader(
            Collection<String> mavenBootstrapUberJarPath,
            ClassLoader parent) {

        try {
            return new ClassLoaderUtils.JarClassLoader(
                    parent,
                    PathUtils.toFile(mavenBootstrapUberJarPath));
        } catch (Exception e) {
            throw new RuntimeException("Unable to create JarClassLoader", e);
        }
    }

    public static ClassLoader createMavenArtifactClassLoader(
            Collection<String> mavenBootstrapUberJarPath,
            ClassLoader parent,
            String group,
            String artifact,
            String version) {


        // todo maybe to add caching ...
        ClassLoader mavenBootstrapClassLoader = Loader.createMavenBootstrapClassLoader(
                mavenBootstrapUberJarPath, parent);

        return RuntimeUtils.invokeStaticMethod(
                mavenBootstrapClassLoader,
                "com.mungolab.djvm.bootstrap.maven",
                "Bootstrap",
                ClassLoader.class,
                "createClassLoader",
                LanguageUtils.arrayOf(ClassLoader.class, String.class, String.class, String.class),
                LanguageUtils.arrayOf(parent, group, artifact, version));
    }
}
