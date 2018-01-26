package com.mygcc.api;

import com.mygcc.datacollection.ClassDoesNotExistException;
import com.mygcc.datacollection.Collaboration;
import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.NetworkException;
import com.mygcc.datacollection.StudentNotInClassException;
import com.mygcc.datacollection.Token;
import com.mygcc.datacollection.UnexpectedResponseException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Collaboration resource endpoint.
 *
 * Endpoint resource for accessing collaboration information.
 */
@Path("/1/class")
public class CollaborationResource extends MyGCCResource {
    /**
     * Gets collaboration data using the Collaboration class.
     *
     * @param token Authorization token.
     * @param courseCode the course to get collaboration from.
     * @return Response to client.
     */
    @Path("/{course}/collaboration")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getCollaborationData(
            @HeaderParam("Authorization") final String token,
            @PathParam("course") final String courseCode) {
        Token auth;
        try {
            auth = new Token(token);
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        }

        Collaboration hmw = new Collaboration(auth, courseCode);
        try {
            Map<String, Object> collaborationData = hmw.getData();
            return Response.status(Response.Status.OK)
                    .entity(collaborationData)
                    .type("application/json")
                    .build();
        } catch (UnexpectedResponseException e) {
            return unexpectedResponseException();
        } catch (NetworkException e) {
            return networkException();
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        } catch (ClassDoesNotExistException e) {
            return classDoesNotExistException();
        } catch (StudentNotInClassException e) {
            return  studentNotInClassException();
        }
    }
}
