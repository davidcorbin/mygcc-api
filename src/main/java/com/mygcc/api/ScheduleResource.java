package com.mygcc.api;

import com.mygcc.datacollection.Authorization;
import com.mygcc.datacollection.Schedule;
import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.UnexpectedResponseException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Schedule resource endpoint.
 *
 * Endpoint resource for accessing schedule information.
 */
@Path("/1/user")
public class ScheduleResource extends MyGCCResource {
    /**
     * Gets schedule data using the Schedule class.
     *
     * @param token Authorization token
     * @return Response to client
     */
    @Path("/schedule")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Response getScheduleData(
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

        Schedule sch = new Schedule(auth);
        try {
            List<Object> scheduleData = sch.getScheduleData();
            return Response.status(Response.Status.OK)
                    .entity(scheduleData)
                    .type("application/json")
                    .build();
        } catch (UnexpectedResponseException e) {
            return unexpectedResponseException();
        }
    }
}
