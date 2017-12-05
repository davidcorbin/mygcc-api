package com.mygcc.api;

import com.mygcc.datacollection.InvalidCredentialsException;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for myGCC resources.
 */
abstract class MyGCCResource {
    /**
     * Default messages returned by the API.
     */
    public enum Message {
        /**
         * Welcome message.
         */
        WELCOME("Welcome to the Unofficial myGCC API"),

        /**
         * Internal server error message.
         */
        INTERNALSERVERERROR("Internal server error"),

        /**
         * Network error message.
         */
        NETWORKEXCEPTION("myGCC is unavailable"),

        /**
         * Invalid credentials message.
         */
        INVALIDCREDENTIALS("Invalid myGCC credentials"),

        /**
         * Invalid password message.
         */
        INVALIDPASSWORD("Password may not contain '&#124;'"),

        /**
         * Session expired message.
         */
        SESSIONEXPIRED("Session expired"),

        /**
         * Class does not exist message.
         */
        NOCLASSEXISTS("Class does not exist"),

        /**
         * Student not in class message.
         */
        STUDENTNOTINCLASS("Student not enrolled in class");

        /**
         * Message.
         */
        private String message;

        /**
         * Set message.
         * @param msg message
         */
        Message(final String msg) {
            this.message = msg;
        }

        /**
         * Get identifier.
         * @return identifier
         */
        public String message() {
            return message;
        }
    }

    /**
     * Tell client that myGCC returned an unknown response.
     * @return Response object
     */
    public Response unexpectedResponseException() {
        return sendErrorMessage(Message.INTERNALSERVERERROR.message(),
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that myGCC is unavailable.
     * @return Response object
     */
    public Response networkException() {
        return sendErrorMessage(Message.NETWORKEXCEPTION.message(),
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that their myGCC login credentials are invalid.
     * @return Response object
     */
    public Response invalidCredentialsException() {
        return sendErrorMessage(Message.INVALIDCREDENTIALS.message(),
                Response.Status.UNAUTHORIZED);
    }

    /**
     * Tell client that their myGCC login credentials are invalid.
     * @return Response object
     */
    public Response classDoesNotExistException() {
        return sendErrorMessage(Message.NOCLASSEXISTS.message(),
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that their myGCC login credentials are invalid.
     * @return Response object
     */
    public Response studentNotInClassException() {
        return sendErrorMessage(Message.STUDENTNOTINCLASS.message(),
                Response.Status.BAD_REQUEST);
    }

    /**
     * Tell client that their myGCC login credentials are invalid with message.
     * @param e InvalidCredentialsException object with message to print
     * @return Response object
     */
    public Response invalidCredentialsException(
            final InvalidCredentialsException e) {
        if (e.getMessage().equals(Message.INVALIDPASSWORD.message())) {
            return sendErrorMessage(e.getMessage(),
                    Response.Status.UNAUTHORIZED);
        }
        return invalidCredentialsException();
    }

    /**
     * Tell client that their session has expired.
     * @return Response object
     */
    public Response sessionExpiredMessage() {
        return sendErrorMessage(Message.SESSIONEXPIRED.message(),
                Response.Status.BAD_REQUEST);
    }

    /**
     * Send error message to client.
     * @param message message text
     * @param status HTTP status code
     * @return Response object
     */
    public final Response sendErrorMessage(final String message,
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
