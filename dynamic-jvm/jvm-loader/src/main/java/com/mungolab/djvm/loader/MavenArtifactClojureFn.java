package com.mungolab.djvm.loader;

import clojure.java.api.Clojure;
import clojure.lang.Compiler;
import clojure.lang.RT;
import clojure.lang.Var;
import com.mungolab.djvm.common.LanguageUtils;
import com.mungolab.djvm.common.PathUtils;
import com.mungolab.djvm.common.RuntimeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class MavenArtifactClojureFn {
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
                MavenArtifactClojureFn.class.getClassLoader(),
                group,
                artifact,
                version);

        // because of RT::baseLoader
        Thread.currentThread().setContextClassLoader(artifactClassLoader);

        Compiler.eval(Clojure.read("(println \"Clojure bootstrap\")"));

        // with this whole loading process fails ...
        Var.pushThreadBindings(RT.map(Compiler.LOADER, artifactClassLoader));
        
        try {
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
        } finally {
            Var.popThreadBindings();
        }
    }
}
