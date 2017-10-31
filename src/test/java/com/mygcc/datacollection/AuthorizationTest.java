package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
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
     * @throws NetworkException Bad connection to mygcc
     */
    @Test
    public void testGetNewSessionID() throws NetworkException {
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
    public void testGetASPXAuth() throws InvalidCredentialsException,
            UnexpectedResponseException {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null);

        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");

        Authorization auth = new Authorization(un, pw);
        String st = auth.getASPXAuth();
        assertTrue("ASPXAUTH cookie should be more than 1 character", st.length() > 1);
    }

    /**
     * Test getting ASPXAUTH without valid myGCC credentials.
     */
    @Test
    public void testGetASPXAuthWOValidCred() throws
            UnexpectedResponseException {
        try {
            Authorization auth = new Authorization("test", "test");
            auth.getASPXAuth();
        } catch (InvalidCredentialsException e) {
            assertTrue(e instanceof InvalidCredentialsException);
        }
    }

    /**
     * Test getting ASPXAUTH without valid myGCC credentials.
     */
    @Test
    public void testGetASPXAuthEmpty() throws UnexpectedResponseException {
        try {
            Authorization auth = new Authorization("", "");
            auth.getASPXAuth();
        } catch (InvalidCredentialsException e) {
            assertTrue(e instanceof InvalidCredentialsException);
        }
    }

    /**
     * Test getting ASPXAUTH without myGCC credentials.
     */
    @Test
    public void testGetASPXAuthWOCred() throws UnexpectedResponseException {
        try {
            Authorization auth = new Authorization();
            auth.getASPXAuth();
        } catch (InvalidCredentialsException e) {
            assertTrue(e instanceof InvalidCredentialsException);
        }
    }

    /**
     * Test encrypting token.
     */
    @Test
    public void testEncryptToken() throws InvalidCredentialsException,
            UnexpectedResponseException {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");

        Authorization auth = new Authorization(un, pw);
        String token = auth.encryptToken();
        assertTrue(token.length() > 1);
    }

    /**
     * Test decrypting token.
     */
    @Test
    public void testDecryptToken() {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");

        Authorization auth = new Authorization(un, pw);

        try {
            auth.decryptToken("testfakedata");
            assertTrue(false);
        } catch (InvalidCredentialsException e) {
            assertTrue(true);
        }
    }

    /**
     * Test token encryption and decryption.
     */
    @Test
    public void testEncryptDecryptToken() throws InvalidCredentialsException,
            UnexpectedResponseException {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");

        Authorization auth = new Authorization(un, pw);
        String token = auth.encryptToken();
        auth.decryptToken(token);

        assertTrue("session ID longer than 1", auth.getSessionID().length() > 1);
    }

    /**
     * Test returning previously set ASPXAuth.
     */
    @Test
    public void testPreviouslyRetrievedASPXAuth() throws
            InvalidCredentialsException, UnexpectedResponseException {
        String testStr = "random_data";
        Authorization auth = new Authorization();
        auth.setASPXAuth(testStr);
        assertTrue(auth.getASPXAuth().equals(testStr));
    }
}
