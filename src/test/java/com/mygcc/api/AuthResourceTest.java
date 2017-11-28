package com.mygcc.api;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

public final class AuthResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(WelcomeResource.class);
    }

    /**
     * Test for success code on normal auth request.
     */
    @Test
    public void testAuthenticateUserStatus() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        AuthResource ar = new AuthResource();
        User u = new User(un, pw);
        Response r = ar.authenticateUser(u);
        assertEquals("test for status code 200", r.getStatus(), Response.Status.OK.getStatusCode());
    }

    /**
     * Test for error code when sending null username.
     */
    @Test
    public void testAuthenticateNullUsername() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        AuthResource ar = new AuthResource();
        User u = new User(un, pw);
        u.setUsername(null);
        Response r = ar.authenticateUser(u);
        assertEquals("test for status code 401 when username is null", r.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    /**
     * Test for error code when sending null password.
     */
    @Test
    public void testAuthenticateNullPassword() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        AuthResource ar = new AuthResource();
        User u = new User(un, pw);
        u.setPassword(null);
        Response r = ar.authenticateUser(u);
        assertEquals("test for status code 401 when password is null", r.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    /**
     * Test for error code when user is null.
     */
    @Test
    public void testAuthenticateNullUser() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        AuthResource ar = new AuthResource();
        Response r = ar.authenticateUser(null);
        assertEquals("test for status code 401 when user is null", r.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }

    /**
     * Test for error code when user is null.
     */
    @Test
    public void testAuthenticateIncorrectCredentials() {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        AuthResource ar = new AuthResource();
        User u = new User("asdf", "asdf");
        Response r = ar.authenticateUser(u);
        assertEquals("test for status code 401 when incorrect login", r.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
