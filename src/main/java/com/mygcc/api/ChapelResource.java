package com.mygcc.api;

import com.mygcc.datacollection.Chapel;
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
 * Chapel resource endpoint.
 *
 * Endpoint resource for accessing chapel information.
 */
@Path("/1/user")
public class ChapelResource extends MyGCCResource {
    /**
     * Handles authenticating user and returning their encrypted token.
     *
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/chapel")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getChapelData(
            @HeaderParam("Authorization") final String token) {
        try {
            Token auth = new Token(token);
            Chapel chap = new Chapel(auth);
            Map<String, Integer> chapelData = chap.getChapelData();
            return Response.status(Response.Status.OK)
                    .entity(chapelData)
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
