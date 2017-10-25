package com.mygcc.api;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import org.glassfish.jersey.test.TestProperties;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Map;

/**
 * Welcome Resource test class.
 */
public final class WelcomeResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(WelcomeResource.class);
    }

    /**
     * Test Welcome message response status.
     */
    @Test
    public void testWelcomeStatus() {
        final Response response = target().path("/").request().get();
        int status = response.getStatus();
        assertEquals("should return status code 200",
                Response.Status.OK.getStatusCode(),
                status);
    }

    /**
     * Test Welcome message response type.
     */
    @Test
    public void testWelcomeType() {
        final Response response = target().path("/").request().get();
        MediaType mt = response.getMediaType();
        assertEquals("should return application/json",
                "application/json",
                mt.toString());
    }

    /**
     * Test Welcome message response data.
     */
    @Test
    public void testWelcomeMessage() {
        final String response = target().path("/").request().get(String.class);
        JSONObject obj = new JSONObject(response);
        assertEquals("should return welcome message",
                "Welcome to the Unofficial myGCC API v1",
                obj.getString("message"));
    }

    /**
     * Test Welcome message response data.
     */
    @Test
    public void testWelcomeDate() {
        final String response = target().path("/").request().get(String.class);
        JSONObject obj = new JSONObject(response);
        boolean geq = Instant.now().getEpochSecond() >= obj.getInt("date");
        assertTrue("should return current UTC time", geq);
    }

    /**
     * Test Welcome returned data.
     */
    @Test
    public void testWelcomeReturnedData() {
        WelcomeResource wr = new WelcomeResource();
        Map<String, Object> returned = wr.getWelcomeMessage();
        assertEquals("Welcome to the Unofficial myGCC API v1", wr.getWelcomeMessage().get("message")); //returned.get("message"));
        boolean geq = Instant.now().getEpochSecond() >= (long)returned.get("date");
        assertTrue(geq);
    }
}
