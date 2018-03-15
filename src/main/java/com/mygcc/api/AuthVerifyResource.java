package com.mygcc.api;

import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.Token;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Verify Authorization resource endpoint.
 *
 * Endpoint resource for verifying the authorization token is valid.
 */
@Path("/1/auth")
public class AuthVerifyResource extends MyGCCResource {
    /**
     * Just checks if the token is valid.
     *
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/verify")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response checkAuth(
            @HeaderParam("Authorization") final String token) {
        try {
            Token auth = new Token(token);
            Map<String, Object> data = new HashMap<>();
            data.put("isValid", true);
            return Response.status(Response.Status.OK)
                    .entity(data)
                    .type("application/json")
                    .build();
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        }
    }
}
