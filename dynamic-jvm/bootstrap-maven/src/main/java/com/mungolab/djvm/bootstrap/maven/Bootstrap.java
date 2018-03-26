package com.mungolab.djvm.bootstrap.maven;

import com.mungolab.djvm.common.LanguageUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.DefaultServiceLocator;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class Bootstrap {
    public static ClassLoader createClassLoader(
            ClassLoader parent,
            String group,
            String artifact,
            String version) {

        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        RepositorySystem system = MavenUtils.newRepositorySystem(locator);
        RepositorySystemSession session = MavenUtils.newSession(system);

        Map<String, String> artifactDesc = MavenUtils.createArtifactDesc(group, artifact, version);

        System.out.println("Creating ClassLoader for: " + group + ":" + artifact + ":" + version);

        List<LanguageUtils.Pair<Map<String, String>, File>> runtimeDependencies = MavenUtils.resolveDependencies(
                MavenUtils.defaultRepositories(),
                system,
                session,
                artifactDesc);

        System.out.println("Runtime dependencies");
        runtimeDependencies
                .stream()
                .forEach(pair -> {
                    MavenUtils.printArtifactDesc(System.out, pair.getLeft());
                    System.out.println(pair.getRight().getAbsolutePath());
                });

        ClassLoader classLoader = new URLClassLoader(
                runtimeDependencies
                        .stream()
                        .map(pair -> {
                            return pair.getRight(); })
                        .map(LanguageUtils::fileToUrl)
                        .toArray(URL[]::new),
                parent);

        return classLoader;
    }
}
