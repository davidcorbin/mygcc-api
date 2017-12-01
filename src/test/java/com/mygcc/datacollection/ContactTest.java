package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.util.Map;

public class ContactTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Contact.class);
    }

    @Test
    public void testGetContactData() throws UnexpectedResponseException {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null
                && System.getenv("initvect") != null
                && System.getenv("enckey") != null);

        String un = System.getenv("myGCC_username");
        String pw = System.getenv("myGCC_password");

        Token auth = new Token(un, pw);
        Contact con = new Contact(auth);
        Map<String, Object> data = con.getContactData();
        assert(data.size() == 4);
    }
}
