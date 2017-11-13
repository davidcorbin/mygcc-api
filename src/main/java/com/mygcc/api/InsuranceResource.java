package com.mygcc.api;

import com.mygcc.datacollection.Authorization;
import com.mygcc.datacollection.Insurance;
import com.mygcc.datacollection.InvalidCredentialsException;
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
 * Insurance resource endpoint.
 *
 * Endpoint resource for accessing Insurance information.
 */
@Path("/1/user")
public class InsuranceResource extends MyGCCResource {
    /**
     * Gets Insurance data using Insurance class.
     *
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/insurance")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getInsuranceData(
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

        Insurance ins = new Insurance(auth);
        try {
            Map<String, Object> insuranceData = ins.getInsuranceData();
            return Response.status(Response.Status.OK)
                    .entity(insuranceData)
                    .type("application/json")
                    .build();
        } catch (UnexpectedResponseException e) {
            return unexpectedResponseException();
        }
    }
}
