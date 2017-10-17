package com.mygcc.errorhandling;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler class for 404 exceptions.
 */
@Provider
public class NotFoundExceptionHandler implements
        ExceptionMapper<NotFoundException> {

    /**
     * Generate and return a response to 404 errors.
     * @param ex NotFoundException object
     * @return Response to be delivered to client
     */
    public final Response toResponse(final NotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint not found");
        response.put("date", Instant.now().getEpochSecond());
        return Response.status(Response.Status.NOT_FOUND)
                .entity(response)
                .type("application/json")
                .build();
    }
}
