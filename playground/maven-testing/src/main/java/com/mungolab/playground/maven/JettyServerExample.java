package com.mungolab.playground.maven;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class JettyServerExample {
    public static void main(String[] args) {

        System.out.println("Running Jetty example");

        MavenUtils.runArtifactMain(
                "com.mungolab.saturday-project",
                "simple-http-server",
                "1.0-SNAPSHOT",
        "com.mungolab.sp.apps.httpserver",
        "ServerMain");
    }
}
