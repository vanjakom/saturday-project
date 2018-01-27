package com.mungolab.sp.apps.httpserver;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Vanja Komadinovic ( vanja@vast.com )
 */
public class ServerMain {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting server");

        Server server = new Server(7071);
        server.setHandler(new AbstractHandler() {
            public void handle(
                    String uri,
                    Request request,
                    HttpServletRequest httpServletRequest,
                    HttpServletResponse httpServletResponse) throws IOException, ServletException {

                System.out.println("handling request: " + uri);

                httpServletResponse.getWriter().write("Hello World on: " +  uri + "\n");
                request.setHandled(true);
            }
        });
        server.start();
        server.join();
    }
}
