package com.mygcc.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.ws.rs.core.Application;
import java.net.HttpURLConnection;
import java.net.URL;

import static junit.framework.TestCase.assertEquals;

public final class MainTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Main.class);
    }

    /**
     * Test that server starts normally.
     * @throws Exception generic exception throw when something goes wrong
     */
    @Test
    public void testDefaultConstructor() throws Exception {
        final int serverPort = 8080;

        // Create Server
        Server server = new Server(serverPort);
        ServletContextHandler context = new ServletContextHandler();
        ServletHolder defaultServ = new ServletHolder("default", DefaultServlet.class);
        defaultServ.setInitParameter("resourceBase", System.getProperty("user.dir"));
        defaultServ.setInitParameter("dirAllowed", "true");
        context.addServlet(defaultServ, "/");
        server.setHandler(context);

        // Start Server
        server.start();

        // Test GET
        HttpURLConnection http = (HttpURLConnection) new
                URL("http://localhost:8080/").openConnection();
        http.connect();
        assertEquals("Response Code", http.getResponseCode(), HttpStatus.OK_200);

        // Stop Server
        server.stop();
    }
}
