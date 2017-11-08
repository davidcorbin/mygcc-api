package com.mygcc.api;

import com.mygcc.datacollection.Authorization;
import com.mygcc.datacollection.CrimsonCash;
import com.mygcc.datacollection.ExpiredSessionException;
import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.NetworkException;
import com.mygcc.datacollection.UnexpectedResponseException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Crimson Cash resource endpoint.
 *
 * Endpoint resource for accessing Crimson Cash information.
 */
@Path("/1/user")
public class CrimsonCashResource extends MyGCCResource {
    /**
     * Handles authenticating user and returning their encrypted token.
     *
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/ccash")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getCrimsonCashData(
            @HeaderParam("Authorization") final String token) {
        if (token == null) {
            return invalidCredentialsException();
        }
        Authorization auth = new Authorization();
        try {
            auth.decryptToken(token);
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        }

        CrimsonCash cc = new CrimsonCash(auth);
        try {
            Map<String, Object> ccData = cc.getCrimsonCashData();
            return Response.status(Response.Status.OK)
                    .entity(ccData)
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
