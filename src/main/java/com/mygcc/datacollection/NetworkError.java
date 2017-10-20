package com.mygcc.datacollection;

/**
 * Network error.
 *
 * Thrown when there is an issue connection to my.gcc.edu.
 */
public class NetworkError extends Exception {
    /**
     * Default constructor.
     */
    public NetworkError() {

    }

    /**
     * Constructor with message.
     * @param message message about issue
     */
    public NetworkError(final String message) {
        super(message);
    }
}
