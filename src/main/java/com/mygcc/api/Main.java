package com.mygcc.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * This class launches the web application in an embedded Jetty container. This
 * is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 */
final class Main {

    /**
     * Start the web server.
     *
     * @param args command line arguments.
     * @throws Exception catches errors from running web server.
     */
    public static void main(final String[] args) throws Exception {
        // Look for environmental variable for port or use default
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        final Server server = new Server(Integer.parseInt(webPort));

        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/*");
        serHol.setInitOrder(1);
        serHol.setInitParameter("jersey.config.server.provider.packages",
                "com.mygcc, com.fasterxml.jackson.jaxrs.json");

        server.start();
        server.join();
    }

    /**
     * Fixes Checkstyle error: utility classes should not have a public or
     * default constructor.
     */
    private Main() {
    }
}
