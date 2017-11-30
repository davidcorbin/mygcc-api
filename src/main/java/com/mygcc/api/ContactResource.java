package com.mygcc.api;

import com.mygcc.datacollection.Contact;
import com.mygcc.datacollection.InvalidCredentialsException;
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
 * Contact resource endpoint.
 *
 * Endpoint resource for accessing Contact information.
 */
@Path("/1/user")
public class ContactResource extends MyGCCResource {
    /**
     * Gets Contact data using Contact class.
     *
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/contact")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getContactData(
            @HeaderParam("Authorization") final String token) {
        Token auth;
        try {
            auth = new Token(token);
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        }

        Contact con = new Contact(auth);
        try {
            Map<String, Object> contactData = con.getContactData();
            return Response.status(Response.Status.OK)
                    .entity(contactData)
                    .type("application/json")
                    .build();
        } catch (UnexpectedResponseException e) {
            return unexpectedResponseException();
        }
    }
}
