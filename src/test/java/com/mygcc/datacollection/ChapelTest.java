package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assume;
import org.junit.Test;

import javax.ws.rs.core.Application;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public final class ChapelTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Chapel.class);
    }

    /**
     * Test chapel data length.
     * @throws Exception any of numerous Exceptions
     */
    @Test
    public void testGetChapelDataLength() throws Exception {
        Assume.assumeTrue(System.getenv("myGCC_username") != null
                && System.getenv("myGCC_password") != null);

        Token tok = new Token(
                System.getenv("myGCC_username"),
                System.getenv("myGCC_password"));
        Chapel ch = new Chapel(tok);
        Map<String, Integer> chapdata = ch.getChapelData();
        assertEquals(chapdata.size(), 5);
    }
}
