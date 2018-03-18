package com.mungolab.playground.maven;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Main {
    // DOM

    public static void main(final String[] args) throws Exception {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        RepositorySystem system = MavenUtils.newRepositorySystem(locator);
        RepositorySystemSession session = MavenUtils.newSession(system);
        List<RemoteRepository> repositories = MavenUtils.buildRepositories();


        MavenUtils.ArtifactDesc jettyServerExampleArtifact =  MavenUtils.createArtifactDesc(
                "com.mungolab.saturday-project",
                "simple-http-server",
                "1.0-SNAPSHOT");

        System.out.println("Direct dependencies:");

        List<MavenUtils.ArtifactDesc> dependencies = MavenUtils.getDependencies(
                repositories,
                system,
                session,
                jettyServerExampleArtifact);

        dependencies
                .stream()
                .forEach(dependency -> {
                    MavenUtils.printArtifactDesc(System.out, dependency);
                });

        System.out.println("Runtime dependencies:");

        List<LanguageUtils.Pair<MavenUtils.ArtifactDesc, File>> runtimeDependencies = MavenUtils.resolveDependencies(
                repositories,
                system,
                session,
                jettyServerExampleArtifact);

        runtimeDependencies
                .stream()
                .forEach(pair -> {
                    MavenUtils.printArtifactDesc(System.out, pair.getLeft());
                    System.out.println(pair.getRight().getAbsolutePath());
                });
        
    }


}
