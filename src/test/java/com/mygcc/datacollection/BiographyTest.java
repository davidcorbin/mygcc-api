package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;

import javax.ws.rs.core.Application;

import java.util.Map;

public class BiographyTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Biography.class);
    }

    @Test
    public void testGetFullName() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert (getData().get("name").length() > 1);
    }

    @Test
    public void testGetMajor() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert (getData().get("major").length() > 1);
    }

    @Test
    public void testGetDegree() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("degree").length() > 1);
    }

    @Test
    public void testGetEmail() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("email").length() > 1);
    }

    @Test
    public void testGetBirth() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("birth").length() > 1);
    }

    @Test
    public void testGetMarital() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("marital").length() > 1);
    }

    @Test
    public void testGetGender() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("gender").length() > 1);
    }

    @Test
    public void testGetEthnicity() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("ethnicity").length() > 1);
    }

    @Test
    public void testGetIDNumber() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC-username") != null
                && System.getenv("myGCC-password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        assert(getData().get("ID").length() > 1);
    }

    public Map<String, String> getData() throws ExpiredSessionException,
            UnexpectedResponseException, NetworkException,
            InvalidCredentialsException {
        String un = System.getenv("myGCC-username");
        String pw = System.getenv("myGCC-password");

        Token auth = new Token(un, pw);
        Biography bio = new Biography(auth);
        return bio.getData();
    }
}
