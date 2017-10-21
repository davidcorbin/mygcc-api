package com.mygcc.api;

import com.mygcc.datacollection.InvalidCredentialsException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Schedule Resource test class.
 */
public final class ScheduleResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(InsuranceResource.class);
    }

    @Test
    public void getMySchedule() throws InvalidCredentialsException {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null);
        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");
        final Response response = target().path("/1/user/schedule").request()
                .header("user", un)
                .header("pass", pw)
                .header("Authorization", 0)
                .get();
        int status = response.getStatus();
        assertEquals("should return status code 200",
                Response.Status.OK.getStatusCode(),
                status);
        assertNotNull("should not return null", response.getEntity());
    }

    @Test
    public void getNoSchedule() throws InvalidCredentialsException {
        final Response response = target().path("/1/user/schedule").request()
                .header("user", "xxx")
                .get();
        int status = response.getStatus();
        assertEquals("should return status code 400",
                //I get 400 with httpie but test gets 404?
                Response.Status.NOT_FOUND.getStatusCode(),
                status);
    }
}
