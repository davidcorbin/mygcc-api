package com.mygcc.datacollection;

import org.apache.commons.lang3.StringUtils;

import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Random;

/**
 * myGCC Authorization class.
 *
 * The Authorization class implements logging in to myGCC, usually done by the
 * user.
 */
public class Authorization {

    /**
     * myGCC login URL.
     */
    private static final String BASEURL = "https://my.gcc.edu/ICS/";

    /**
     * myGCC username (usually a six-digit integer).
     */
    private String username;

    /**
     * myGCC password.
     */
    private String password;

    /**
     * myGCC sessionID.
     */
    private String sessionID;

    /**
     * IIS boundary string.
     *
     * The boundary string is a randomly generated string of dashes followed by
     * 25 integers.
     * https://stackoverflow.com/a/20321259/2628694
     */
    private String boundary;

    /**
     * Constructor with username and password.
     * @param un myGCC username
     * @param pw myGCC password
     */
    public Authorization(final String un, final String pw) {
        setUsername(un);
        setPassword(pw);

        setBoundary();
    }

    /**
     * Default constructor.
     */
    public Authorization() {

    }

    /**
     * Get ASPXAUTH cookie.
     * @throws InvalidCredentialsException username or password fail logging
     * into myGCC
     * @return ASPXAUTH cookie string
     */
    public final String getASPXAuth() throws InvalidCredentialsException {
        try {
            String seshid = getSessionID();
            String postdata = authdata();

            // Build connection
            URLConnection con = new URL(BASEURL).openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setInstanceFollowRedirects(false);
            http.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + getBoundary());

            http.setRequestProperty("Cookie",
                    "ASP.NET_SessionId=" + seshid + ";");

            DataOutputStream wr = new DataOutputStream(
                    http.getOutputStream());
            wr.writeBytes(postdata);
            wr.close();

            List<String> cookies = http.getHeaderFields().get("Set-Cookie");

            // Should be a ASPXAUTH cookie and a session heartbeat
            if (cookies.size() != 2) {
                throw new InvalidCredentialsException("2 cookies expected; "
                        + cookies.size() + " received");
            }
            String aspxraw = cookies.get(0);

            // Trim everything but the cookie value itself
            return StringUtils.substringBetween(aspxraw,
                    ".ASPXAUTH=",
                    "; path=/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get POST request body for login form.
     * @return POST request body
     */
    private String authdata() {
        return "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data;"
                + " name=\"_scriptManager_HiddenField\"\n"
                + "\n"
                + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"__EVENTTARGET\"\n"
                + "\n"
                + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"__EVENTARGUMENT\"\n"
                + "\n"
                + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"__VIEWSTATE\"\n"
                + "\n"
                + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data;"
                + " name=\"__VIEWSTATEGENERATOR\"\n"
                + "\n"
                + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"___BrowserRefresh\"\n"
                + "\n"
                + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"userName\"\n"
                + "\n"
                + getUsername() + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"password\"\n"
                + "\n"
                + getPassword() + "\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"btnLogin\"\n"
                + "\n"
                + "Login\n"
                + "--" + getBoundary() + "\n"
                + "Content-Disposition: form-data; name=\"ctl04$tbSearch\"\n"
                + "\n"
                + "Search...\n"
                + "--" + getBoundary() + "--\n";
    }

    /**
     * Set boundary string.
     */
    private void setBoundary() {
        boundary = "---------------------------" + generateBoundary();
    }

    /**
     * Get boundary string.
     * Will generate new boundary if not previously set.
     *
     * @return boundary string
     */
    private String getBoundary() {
        if (boundary == null) {
            setBoundary();
        }
        return boundary;
    }

    /**
     * Generate a 25-digit boundary to include in request.
     * @return 25-digit boundary for IIS
     */
    private String generateBoundary() {
        final String saltchars = "1234567890";
        final int stringlen = 25;

        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < stringlen) {
            int index = (int) (rnd.nextFloat() * saltchars.length());
            salt.append(saltchars.charAt(index));
        }
        return salt.toString();
    }

    /**
     * Gets session ID.
     * @return session ID string
     */
    public final String getSessionID() {
        // Set session ID if not set already
        if (sessionID == null) {
            try {
                setSessionID();
            } catch (NetworkException e) {
                e.printStackTrace();
            }
        }
        return sessionID;
    }

    /**
     * Set session ID from previous session ID.
     * @param sid session ID
     */
    public final void setSessionID(final String sid) {
        this.sessionID = sid;
    }

    /**
     * Gets a new sessionID from myGCC.
     * @throws NetworkException bad connection to my.gcc.edu
     */
    public final void setSessionID() throws NetworkException {
        try {
            URLConnection connection = new URL(BASEURL).openConnection();
            List<String> cookies = connection.
                    getHeaderFields().
                    get("Set-Cookie");
            if (cookies.size() != 2) {
                throw new Exception("Expected 2 cookies from mygcc");
            }
            String seshidstr = cookies.get(1);

            // Get the session ID parameter
            this.sessionID = StringUtils.substringBetween(seshidstr,
                    "ASP.NET_SessionId=",
                    "; path=/; HttpOnly");
        } catch (ConnectException e) {
            throw new NetworkException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get username.
     * @return username
     */
    public final String getUsername() {
        return username;
    }

    /**
     * Set username.
     * @param un username
     */
    private void setUsername(final String un) {
        this.username = un;
    }

    /**
     * Get password string.
     * @return password
     */
    private String getPassword() {
        return password;
    }

    /**
     * Set password.
     * @param pw password string
     */
    private void setPassword(final String pw) {
        this.password = pw;
    }
}
