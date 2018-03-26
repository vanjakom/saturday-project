package com.mungolab.djvm.bootstrap.maven;

import com.mungolab.djvm.common.LanguageUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class MavenUtils {
    public static final String ARTIFACT_DESC_GROUP = "group";
    public static final String ARTIFACT_DESC_ARTIFACT = "artifact";
    public static final String ARTIFACT_DESC_VERSION = "version";

    public static Map<String, String> createArtifactDesc(
            String group,
            String artifact,
            String version) {
        return LanguageUtils.mapOf(
                ARTIFACT_DESC_GROUP, group,
                ARTIFACT_DESC_ARTIFACT, artifact,
                ARTIFACT_DESC_VERSION, version);
    }

    public static Boolean printArtifactDesc(
            OutputStream outputStream,
            Map<String, String> artifactDesc) {
        String output = artifactDesc.get(ARTIFACT_DESC_GROUP) + ":" +
                artifactDesc.get(ARTIFACT_DESC_ARTIFACT) + ":" +
                artifactDesc.get(ARTIFACT_DESC_VERSION);
        try {
            outputStream.write(output.getBytes());
            outputStream.write("\n".getBytes());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Unable to write to output stream", e);
        }
    }

    public static Artifact createArtifact(Map<String, String> artifactDesc) {
        return new DefaultArtifact(
                artifactDesc.get(ARTIFACT_DESC_GROUP) + ":" +
                        artifactDesc.get(ARTIFACT_DESC_ARTIFACT) + ":" +
                        artifactDesc.get(ARTIFACT_DESC_VERSION));
    }

    public static ClassLoader artifactClassLoader(
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

    // https://stackoverflow.com/questions/39638138/find-all-direct-dependencies-of-an-artifact-on-maven-central
    public static List<Map<String, String>> getDependencies(
            List<RemoteRepository> repositories,
            RepositorySystem system,
            RepositorySystemSession session,
            Map<String, String> artifactDesc) {

        try {
            Artifact artifact = MavenUtils.createArtifact(artifactDesc);
            ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, repositories, null);
            ArtifactDescriptorResult result = system.readArtifactDescriptor(session, request);

            return result.getDependencies()
                    .stream()
                    .map(dependency -> {
                        return LanguageUtils.mapOf(
                                ARTIFACT_DESC_GROUP, dependency.getArtifact().getGroupId(),
                                ARTIFACT_DESC_ARTIFACT, dependency.getArtifact().getArtifactId(),
                                ARTIFACT_DESC_VERSION, dependency.getArtifact().getVersion());
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Unable to obtain dependencies", e );
        }
    }

    // https://stackoverflow.com/questions/40813062/maven-get-all-dependencies-programmatically
    public static List<LanguageUtils.Pair<Map<String, String>, File>> resolveDependencies(
            List<RemoteRepository> repositories,
            RepositorySystem system,
            RepositorySystemSession session,
            Map<String, String> artifactDesc) {
        try {

            Artifact artifact = MavenUtils.createArtifact(artifactDesc);

            CollectRequest collectRequest = new CollectRequest(
                    new Dependency(artifact, JavaScopes.COMPILE),
                    repositories);
            DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
            DependencyRequest request = new DependencyRequest(collectRequest, filter);
            DependencyResult result = system.resolveDependencies(session, request);

            return result.getArtifactResults()
                    .stream()
                    .map(artifactResult -> {
                        return
                                LanguageUtils.pairOf(
                                        LanguageUtils.mapOf(
                                                ARTIFACT_DESC_GROUP, artifactResult.getArtifact().getGroupId(),
                                                ARTIFACT_DESC_ARTIFACT, artifactResult.getArtifact().getArtifactId(),
                                                ARTIFACT_DESC_VERSION, artifactResult.getArtifact().getVersion()),
                                        artifactResult.getArtifact().getFile()); })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve dependencies", e);
        }
    }

    public static RepositorySystem newRepositorySystem(DefaultServiceLocator locator) {
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    public static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(System.getProperty("user.home") + "/.m2/repository");
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        // set possible proxies and mirrors
        session.setProxySelector(
                new DefaultProxySelector().add(
                        new Proxy(Proxy.TYPE_HTTP, "host", 3625),
                        Arrays.asList("localhost", "127.0.0.1")));
        session.setMirrorSelector(
                new DefaultMirrorSelector().add(
                        "my-mirror",
                        "http://mirror",
                        "default",
                        false,
                        "external:*",
                        null));
        return session;
    }

    public static RemoteRepository createRepository(String name, String url) {
        return new RemoteRepository.Builder(name, "default", url).build();
    }

    public static List<RemoteRepository> defaultRepositories() {
        return Arrays.asList(
                //createRepository("local",  "http://localhost:8080/maven/"),
                createRepository("central",  "http://repo1.maven.org/maven2/")
        );
    }
}
