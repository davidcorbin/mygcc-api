package com.mygcc.api;

import com.mygcc.api.InsuranceResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Insurance Resource test class.
 */
public final class InsuranceResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(InsuranceResource.class);
    }

    /**
     * Test Insurance response status with valid Id.
     */
    @Test
    public void testValidInsuranceId() {
        final Response response = target().path("/1/user/insurance").request().header("Authorization", 207783).get();
        int status = response.getStatus();
        assertEquals("should return status code 200",
                Response.Status.OK.getStatusCode(),
                status);
        assertNotNull("should not return null", response.getEntity());
    }

    /**
     * Test Insurance response status with missing Id.
     */
    @Test
    public void testMissingInsuranceId() {
        final Response response = target().path("/1/user/insurance").request().get();
        int status = response.getStatus();
        assertEquals("should return status code 400",
                Response.Status.BAD_REQUEST.getStatusCode(),
                status);
    }

    /**
     * Test Insurance response status with invalid Id.
     */
    @Test
    public void testInvalidInsuranceId() {
        final Response response = target().path("/1/user/insurance").request().header("Authorization", "666").get();
        int status = response.getStatus();
        assertEquals("should return status code 404",
                Response.Status.NOT_FOUND.getStatusCode(),
                status);
    }
}
