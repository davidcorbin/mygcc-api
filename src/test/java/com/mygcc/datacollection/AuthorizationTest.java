package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthorizationTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Authorization.class);
    }

    /**
     * Test that session ID string is 24 characters.
     * @throws NetworkError Bad connection to mygcc
     */
    @Test
    public void testGetNewSessionID() throws NetworkError {
        Authorization auth = new Authorization();
        String seshid = auth.getSessionID();
        assertEquals("should be 24 characters long",
                seshid.length(),
                24);
    }

    /**
     * Test getting ASPXAUTH with valid myGCC credentials.
     * @throws InvalidCredentialsException invalid credentials
     */
    @Test
    public void testGetASPXAuth() throws InvalidCredentialsException {
        // Use environment variables if possible
        if (System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null) {
            String un = System.getenv("myGCC-username");
            String pw = System.getenv("myGCC-password");

            Authorization auth = new Authorization(un, pw);
            String st = auth.getASPXAuth();
            assertTrue("ASPXAUTH cookie should be more than 1 character", st.length() > 1);
        }
    }

    /**
     * Test getting ASPXAUTH without valid myGCC credentials.
     */
    @Test
    public void testGetASPXAuthWOValidCred() {
        try {
            Authorization auth = new Authorization("test", "test");
            auth.getASPXAuth();
        } catch (InvalidCredentialsException e) {
            assertTrue(e instanceof InvalidCredentialsException);
        }
    }

    /**
     * Test getting ASPXAUTH without myGCC credentials.
     */
    @Test
    public void testGetASPXAuthWOCred() {
        try {
            Authorization auth = new Authorization();
            auth.getASPXAuth();
        } catch (InvalidCredentialsException e) {
            assertTrue(e instanceof InvalidCredentialsException);
        }
    }
}
