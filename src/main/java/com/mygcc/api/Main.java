package com.mygcc.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

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

        final Server server = new Server(Integer.valueOf(webPort));
        final WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        // Parent loader priority is a class loader setting that Jetty accepts.
        // By default Jetty will behave like most web containers in that it will
        // allow your application to replace non-server libraries that are part
        // of the container. Setting parent loader priority to true changes this
        // behavior.
        // Read more here:
        // http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);

        final String webappDirLocation = "src/main/webapp/";
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        server.setHandler(root);

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
