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
 * Chapel resource endpoint.
 *
 * Endpoint resource for accessing chapel information.
 */
@Path("/1/user")
public class InsuranceResource {
    /**
     * Handles authenticating user and returning their encrypted token.
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
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Missing authorization token");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .type("application/json")
                    .build();
        }
        Authorization auth = new Authorization();
        try {
            auth.decryptToken(token);
        } catch (InvalidCredentialsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .type("application/json")
                    .build();
        }

        Insurance ins = new Insurance(auth);
        try {
            Map<String, Object> chapelData = ins.getInsuranceData();
            return Response.status(Response.Status.OK)
                    .entity(chapelData)
                    .type("application/json")
                    .build();
        } catch (UnexpectedResponseException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Internal server error");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .type("application/json")
                    .build();
        }
    }
}
