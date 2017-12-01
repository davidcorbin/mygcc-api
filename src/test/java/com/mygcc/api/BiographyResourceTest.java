package com.mygcc.api;

import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.Token;
import com.mygcc.datacollection.UnexpectedResponseException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class BiographyResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(BiographyResource.class);
    }

    @Test
    public void testBiographyStatus() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        Token auth = new Token(un, pw);
        try {
            String token = auth.encrypt();

            BiographyResource br = new BiographyResource();
            Response r = br.getAllData(token);
            assertEquals("test success status", Response.Status.OK.getStatusCode(), r.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBiographyStatusInvalidPassword() throws InvalidCredentialsException {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        Token auth = new Token("asdf", "asdf");
        String token = auth.encrypt();

        BiographyResource br = new BiographyResource();
        Response r = br.getAllData(token);
        assertEquals("test unauthorized status code", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

    @Test
    public void testBiographyStatusNullToken() {
        BiographyResource br = new BiographyResource();
        Response r = br.getAllData(null);
        assertEquals("test unauthorized status code", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }
}
