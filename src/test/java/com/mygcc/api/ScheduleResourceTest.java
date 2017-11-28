package com.mygcc.api;

import com.mygcc.datacollection.Token;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public final class ScheduleResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(ScheduleResource.class);
    }

    /**
     * Test when token is null.
     */
    @Test
    public void testNullAuthorization() {
        ScheduleResource sch = new ScheduleResource();
        Response r = sch.getScheduleData(null);
        assertEquals("status should be 400", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

    /**
     * Test when key is null.
     */
    @Test
    public void testFakeAuth() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);
        ScheduleResource sch = new ScheduleResource();
        Response r = sch.getScheduleData("asdf");
        assertEquals("token should be invalid; status should be 400", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

    /**
     * Test when token is valid.
     */
    @Test
    public void testWorkingKey() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);
        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        Token auth = new Token(un, pw);
        try {
            String token = auth.encrypt();

            ScheduleResource sch = new ScheduleResource();
            Response r = sch.getScheduleData(token);
            assertEquals("test working key", Response.Status.OK.getStatusCode(), r.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
