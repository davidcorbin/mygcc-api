package com.mygcc.api;

import com.mygcc.datacollection.Authorization;
import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.UnexpectedResponseException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthResource class handles myGCC login credentials.
 */
@Path("/1/auth")
public class AuthResource extends MyGCCResource {
    /**
     * Handles authenticating user and returning their encrypted token.
     *
     * @param user User object
     * @return Response to client
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response authenticateUser(final User user) {
        // Check that user is not null and that required parameters exist
        if (user == null || !user.checkRequiredParams()) {
            return invalidCredentialsException();
        }

        String username = user.getUsername();
        String password = user.getPassword();

        Authorization auth = new Authorization(username, password);
        String token;
        try {
            token = auth.encryptToken();
            Map<String, Object> usermap = new HashMap<>();
            usermap.put("date", Instant.now().getEpochSecond());
            usermap.put("token", token);
            return Response.status(Response.Status.OK)
                    .entity(usermap)
                    .type("application/json")
                    .build();
        } catch (InvalidCredentialsException e) {
            return invalidCredentialsException();
        } catch (UnexpectedResponseException e) {
            return unexpectedResponseException();
        }
    }
}

