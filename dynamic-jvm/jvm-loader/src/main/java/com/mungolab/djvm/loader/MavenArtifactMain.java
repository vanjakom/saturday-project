package com.mungolab.djvm.loader;

import com.mungolab.djvm.common.LanguageUtils;
import com.mungolab.djvm.common.PathUtils;
import com.mungolab.djvm.common.RuntimeUtils;

import java.util.Arrays;
import java.util.List;

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

        if (args.length > 5) {
            System.out.println("Running main method in class: " + mainClass + " with args");

            List<String> mainArgs = LanguageUtils.dropN(Arrays.asList(args), 5);
            System.out.println("Main args:");
            mainArgs.stream().forEach(arg -> System.out.println(arg));

            RuntimeUtils.invokeMainMethodWithArgs(artifactClassLoader, mainClass, mainArgs.toArray(new String[0]));

        } else {
            System.out.println("Running main method in class: " + mainClass);
            RuntimeUtils.invokeMainMethod(artifactClassLoader, mainClass);
        }
    }
}
