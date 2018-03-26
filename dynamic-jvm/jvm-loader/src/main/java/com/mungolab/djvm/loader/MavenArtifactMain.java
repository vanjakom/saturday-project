package com.mungolab.djvm.loader;

import com.mungolab.djvm.common.PathUtils;
import com.mungolab.djvm.common.RuntimeUtils;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class MavenArtifactMain {
    public static void main(String[] args) {
        String mavennBootstrapUBerJar = args[0];
        String group = args[1];
        String artifact = args[2];
        String version = args[3];
        String mainClass = args[4];


        System.out.println("Using Maven bootstrap jar: " + mavennBootstrapUBerJar);
        System.out.println("Resolving artifact: " + group + ":" + artifact + ":" + version);
        ClassLoader artifactClassLoader = Loader.createMavenArtifactClassLoader(
                PathUtils.fromString(mavennBootstrapUBerJar),
                MavenArtifactMain.class.getClassLoader(),
                group,
                artifact,
                version);

        System.out.println("Running main method in class: " + mainClass);
        RuntimeUtils.invokeMainMethod(artifactClassLoader, mainClass);
    }
}
