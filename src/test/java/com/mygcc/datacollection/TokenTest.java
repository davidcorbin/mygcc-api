package com.mygcc.datacollection;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;

public final class TokenTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(Token.class);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testNullToken() throws InvalidCredentialsException {
        new Token(null);
    }

}
