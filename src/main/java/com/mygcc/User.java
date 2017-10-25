package com.mygcc;

/**
 * User class.
 */
public class User {
    /**
     * Username string.
     */
    private String username;

    /**
     * Password string.
     */
    private String password;

    /**
     * Session ID string.
     */
    private String sessionid;

    /**
     * User constructor.
     * @param un username
     * @param pw password
     * @param ssid session ID
     */
    public User(final String un, final String pw, final String ssid) {
        this.username = un;
        this.password = pw;
        this.sessionid = ssid;
    }

    /**
     * Default constructor.
     */
    public User() {

    }

    /**
     * Get username.
     * @return username string
     */
    public final String getUsername() {
        return this.username;
    }

    /**
     * Set username.
     * @param un username
     */
    public final void setUsername(final String un) {
        this.username = un;
    }

    /**
     * Get password.
     * @return password string
     */
    public final String getPassword() {
        return this.password;
    }

    /**
     * Set password.
     * @param pw password string
     */
    public final void setPassword(final String pw) {
        this.password = pw;
    }

    /**
     * Get session identifier.
     * @return session ID string
     */
    public final String getSessionid() {
        return this.sessionid;
    }

    /**
     * Set session identifier.
     * @param sid session ID
     */
    public final void setSessionid(final String sid) {
        this.sessionid = sid;
    }

    /**
     * Check if any member variables are null.
     * @return whether any values are null
     */
    public final boolean checkRequiredParams() {
        return !(username == null || password == null);
    }
}
