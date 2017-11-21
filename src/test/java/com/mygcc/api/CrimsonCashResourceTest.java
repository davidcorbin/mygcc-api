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

public final class CrimsonCashResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(WelcomeResource.class);
    }

    /**
     * Test when token is null.
     */
    @Test
    public void testNullAuthorization() {
        CrimsonCashResource cc = new CrimsonCashResource();
        Response r = cc.getCrimsonCashData(null);
        assertEquals("status should be 401", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

    /**
     * Test when key is null.
     */
    @Test
    public void testFakeAuth() {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);
        CrimsonCashResource cc = new CrimsonCashResource();
        Response r = cc.getCrimsonCashData("asdf");
        assertEquals("token should be invalid; status should be 400", Response.Status.UNAUTHORIZED.getStatusCode(), r.getStatus());
    }

    /**
     * Test when token is valid.
     */
    @Test
    public void testWorkingKey() {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);
        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");

        Token auth = new Token(un, pw);
        try {
            String token = auth.encrypt();

            CrimsonCashResource cc = new CrimsonCashResource();
            Response r = cc.getCrimsonCashData(token);
            assertEquals("test working key", Response.Status.OK.getStatusCode(), r.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
