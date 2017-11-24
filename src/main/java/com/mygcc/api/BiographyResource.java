package com.mygcc.api;

import com.mygcc.datacollection.Biography;
import com.mygcc.datacollection.ExpiredSessionException;
import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.NetworkException;
import com.mygcc.datacollection.Token;
import com.mygcc.datacollection.UnexpectedResponseException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Get data about client.
 */
@Path("/1/user")
public class BiographyResource extends MyGCCResource {
    /**
     * Get all data about client.
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getAllData(
            @HeaderParam("Authorization") final String token) {
        Token auth;

        // Try to decrypt token sent by client
        try {
            auth = new Token(token);
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        }

        Biography bio = new Biography(auth);
        try {
            Map<String, String> data = bio.getData();
            return Response.status(Response.Status.OK)
                    .entity(data)
                    .type("application/json")
                    .build();
        } catch (ExpiredSessionException e) {
            return sessionExpiredMessage();
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        } catch (NetworkException e) {
            return networkException();
        } catch (UnexpectedResponseException e) {
            return unexpectedResponseException();
        }
    }
}
