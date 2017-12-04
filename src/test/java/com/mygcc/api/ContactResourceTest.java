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

public final class ContactResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(ContactResource.class);
    }

    @Test
    public void testNullAuthorization() {
        ContactResource conres = new ContactResource();
        Response r = conres.getContactData(null);
        assertEquals("status should be unauthorized", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

    @Test
    public void testFakeAuth() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);
        ContactResource conres = new ContactResource();
        Response r = conres.getContactData("asdf");
        assertEquals("status should be unauthorized", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

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

            ContactResource conres = new ContactResource();
            Response r = conres.getContactData(token);
            assertEquals("test working key", Response.Status.OK.getStatusCode(), r.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
