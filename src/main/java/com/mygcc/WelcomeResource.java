package com.mygcc.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Welcome message for root domain.
 */
@Path("/")
public class WelcomeResource {

    /**
     * Welcome message.
     */
    private final String welcomeMsg = "Welcome to the Unofficial myGCC API v1";

    /**
     * Method handling HTTP GET requests to the root domain.
     *
     * @return String that will be returned as a application/json response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Map<String, Object> getWelcomeMessage() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", welcomeMsg);
        response.put("date", Instant.now().getEpochSecond());
        return response;
    }
}
