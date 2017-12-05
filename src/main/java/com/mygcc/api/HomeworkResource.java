package com.mygcc.api;

import com.mygcc.datacollection.*;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Homework resource endpoint.
 *
 * Endpoint resource for accessing homework information.
 */
@Path("/1/class")
public class HomeworkResource extends MyGCCResource {
    /**
     * Gets homework data using the Homework class.
     *
     * @param token Authorization token.
     * @param courseCode the course to get homework from.
     * @return Response to client.
     */
    @Path("/{course}/homework")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getHomeworkData(
            @HeaderParam("Authorization") final String token,
            @PathParam("course") final String courseCode) {
        Token auth;
        try {
            auth = new Token(token);
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        }

        Homework hmw = new Homework(auth, courseCode);
        try {
            List<Object> homeworkData = hmw.getHomeworkData();
            return Response.status(Response.Status.OK)
                    .entity(homeworkData)
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
