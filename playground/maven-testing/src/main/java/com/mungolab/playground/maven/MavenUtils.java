package com.mungolab.playground.maven;

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
import java.util.stream.Collectors;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class MavenUtils {
    public static class ArtifactDesc extends LanguageUtils.MapProxy<String, String> {
        public static final String GROUP = "group";
        public static final String NAME = "name";
        public static final String VERSION = "version";

        public ArtifactDesc(String group, String name, String version) {
            super(LanguageUtils.mapOf(
                    GROUP, group,
                    NAME, name,
                    VERSION, version));
        }

        public String getGroup() {
            return this.get(GROUP);
        }

        public String getName() {
            return this.get(NAME);
        }

        public String getVersion() {
            return this.get(VERSION);
        }

        public ArtifactDesc withGroup(String group) {
            return new ArtifactDesc(group, this.getName(), this.getVersion());
        }

        public ArtifactDesc withName(String name) {
            return new ArtifactDesc(this.getGroup(), name, this.getVersion());
        }

        public ArtifactDesc withVersion(String version) {
            return new ArtifactDesc(this.getGroup(), this.getName(), version);
        }
    }

    // https://stackoverflow.com/questions/39638138/find-all-direct-dependencies-of-an-artifact-on-maven-central
    public static List<ArtifactDesc> getDependencies(
            List<RemoteRepository> repositories,
            RepositorySystem system,
            RepositorySystemSession session,
            ArtifactDesc artifactDesc) {

        try {
            Artifact artifact = createArtifact(artifactDesc);
            ArtifactDescriptorRequest request = new ArtifactDescriptorRequest(artifact, repositories, null);
            ArtifactDescriptorResult result = system.readArtifactDescriptor(session, request);

            return result.getDependencies()
                    .stream()
                    .map(dependency -> {
                        return createArtifactDesc(
                                dependency.getArtifact().getGroupId(),
                                dependency.getArtifact().getArtifactId(),
                                dependency.getArtifact().getVersion());
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Unable to obtain dependencies", e );
        }
    }

    // https://stackoverflow.com/questions/40813062/maven-get-all-dependencies-programmatically
    public static List<LanguageUtils.Pair<ArtifactDesc, File>> resolveDependencies(
            List<RemoteRepository> repositories,
            RepositorySystem system,
            RepositorySystemSession session,
            ArtifactDesc artifactDesc) {
        try {

            Artifact artifact = createArtifact(artifactDesc);

            CollectRequest collectRequest = new CollectRequest(new Dependency(artifact, JavaScopes.COMPILE), repositories);
            DependencyFilter filter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
            DependencyRequest request = new DependencyRequest(collectRequest, filter);
            DependencyResult result = system.resolveDependencies(session, request);

            return result.getArtifactResults()
                    .stream()
                    .map(artifactResult -> {
                        return LanguageUtils.pairOf(
                                new ArtifactDesc(
                                        artifactResult.getArtifact().getGroupId(),
                                        artifactResult.getArtifact().getArtifactId(),
                                        artifactResult.getArtifact().getVersion()),
                                artifactResult.getArtifact().getFile()); })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve dependencies", e);
        }
    }

    public static ClassLoader artifactClassLoader(
            String group,
            String artifact,
            String version) {

        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        RepositorySystem system = MavenUtils.newRepositorySystem(locator);
        RepositorySystemSession session = MavenUtils.newSession(system);

        ArtifactDesc artifactDesc = new ArtifactDesc(group, artifact, version);

        System.out.println("Creating ClassLoader for: " + group + ":" + artifact + ":" + version);

        List<LanguageUtils.Pair<MavenUtils.ArtifactDesc, File>> runtimeDependencies = MavenUtils.resolveDependencies(
                buildRepositories(),
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
                        .toArray(URL[]::new));

        return classLoader;
    }

    public static void runArtifactMain(
            String group,
            String artifact,
            String version,
            String packageS,
            String mainClassS) {

        ClassLoader classLoader = MavenUtils.artifactClassLoader(group, artifact, version);

        System.out.println("Main class: " + packageS + "." + mainClassS);

        RuntimeUtils.runMainMethod(classLoader, packageS + "." + mainClassS);
    }

    public static List<RemoteRepository> buildRepositories() {
        return Arrays.asList(
                //createRepository("local",  "http://localhost:8080/maven/"),
                createRepository("central",  "http://repo1.maven.org/maven2/")
        );
    }

    public static RemoteRepository createRepository(String name, String url) {
        return new RemoteRepository.Builder(name, "default", url).build();
    }

    public static ArtifactDesc createArtifactDesc(
            String group,
            String name,
            String version) {
        return new ArtifactDesc(group, name, version);
    }

    public static Boolean printArtifactDesc(
            OutputStream outputStream,
            ArtifactDesc artifactDesc) {
        String output = artifactDesc.getGroup() + ":" +
                artifactDesc.getName() + ":" +
                artifactDesc.getVersion();
        try {
            outputStream.write(output.getBytes());
            outputStream.write("\n".getBytes());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Unable to write to output stream", e);
        }
    }

    public static Artifact createArtifact(ArtifactDesc artifactDesc) {
        return new DefaultArtifact(
                artifactDesc.getGroup() + ":" +
                        artifactDesc.getName() + ":" +
                        artifactDesc.getVersion());
    }

    public static RepositorySystem newRepositorySystem(DefaultServiceLocator locator) {
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    public static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(System.getProperty("user.home") + ".m2/repository");
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
}
