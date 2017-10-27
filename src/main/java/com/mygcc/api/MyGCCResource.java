package com.mygcc.api;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for myGCC resources.
 */
abstract class MyGCCResource {
    /**
     * Tell client that myGCC returned an unknown response.
     * @return Response object
     */
    Response unexpectedResponseException() {
        return sendErrorMessage("Internal server error",
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that myGCC is unavailable.
     * @return Response object
     */
    Response networkException() {
        return sendErrorMessage("myGCC is unavailable",
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that their myGCC login credentials are invalid.
     * @return Response object
     */
    Response invalidCredentialsException() {
        return sendErrorMessage("Invalid myGCC credentials",
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that their session has expired.
     * @return Response object
     */
    Response sessionExpiredMessage() {
        return sendErrorMessage("Session expired",
                Response.Status.BAD_REQUEST);
    }

    /**
     * Send error message to client.
     * @param message message text
     * @param status HTTP status code
     * @return Response object
     */
    final Response sendErrorMessage(final String message,
                                     final Response.Status status) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("date", Instant.now().getEpochSecond());
        return Response.status(status)
                .entity(response)
                .type("application/json")
                .build();
    }
}
