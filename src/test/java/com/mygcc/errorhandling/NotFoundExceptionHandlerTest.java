package com.mygcc.errorhandling;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Not Found Exception Handler test class.
 */
public final class NotFoundExceptionHandlerTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(NotFoundExceptionHandler.class);
    }

    /**
     * Test invalid endpoint status.
     */
    @Test
    public void testNotFoundStatus() {
        final Response response = target().path("/notfound").request().get();
        int status = response.getStatus();
        assertEquals("should return status code 404",
                Response.Status.NOT_FOUND.getStatusCode(),
                status);
    }
}
