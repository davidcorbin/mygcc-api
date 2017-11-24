package com.mygcc.datacollection;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * Implements authorization into myGCC, usually done manually by the user.
 *
 * <h3>Overview</h3>
 * <p>When the user logs in to myGCC, the server gives their browser a session
 * from which it can identify the browser and keep the user logged in. The
 * {@code Authorization} class simulates such functionality.</p>
 *
 * <h3>Internals</h3>
 * <p>Creating the session has several steps:</p>
 * <ol>
 *     <li>Retrieve session identifier</li>
 *     <li>Authenticate client</li>
 *     <li>Get session data</li>
 * </ol>
 *
 * <h4>Retrieve session identifier</h4>
 * <p>The
 * <a target="_blank" href="http://bit.ly/2zxIxJ8">session identifier</a>,
 * {@code sessionID}, is a cookie that is returned from Microsoft IIS server
 * to identify a browser. The session identifier is sent whether or not the
 * client is logged in and persists forever in the browser. To get the session
 * identifier, send an HTTP GET request to the {@code BASEURL} and retrieve it
 * from the returned cookies.</p>
 *
 * <h4>Authenticate client</h4>
 * <p>While retrieving the session identifier doesn't require any credentials,
 * authenticating the client requires their myGCC username and password. The
 * myGCC authentication process begins by logging the user in. Logon is done by
 * sending an HTTP POST request with the specified username and password to the
 * {@code BASEURL}. The logon request has to be in the valid IIS format with a
 * boundary string separating each section and specified in the request header.
 * If the username and password were correct, the response should contain a new
 * cookie, {@code aspxauth}, which is associated with the client's session can
 * be used to determine whether the client is logged in. In addition to the
 * {@code aspxauth} cookie, there are several hidden HTML input elements that
 * can be used to determine the state of the page. These elements include
 * {@code #___BrowserRefresh}, {@code #__VIEWSTATE},
 * {@code #_scriptManager_HiddenField}, {@code #__EVENTTARGET}, and
 * {@code #__EVENTARGUMENT}. The only elements that seem to be used are
 * {@code #___BrowserRefresh} and {@code #__VIEWSTATE} and are passed in HTTP
 * POST request data do the server.</p>
 *
 * <h3>Security</h3>
 * <p>To maximize the security of client login credentials, username and
 * password do not persist beyond the request. Credentials are not stored in a
 * database or persistent mechanism in order to limit attack vectors.</p>
 */
public class Session extends MyGCCDataCollection {
    /**
     * myGCC login URL.
     */
    private static final String BASEURL = "https://my.gcc.edu/ics/";

    /**
     * Token object.
     */
    private Token token;

    /**
     * myGCC session identifier.
     */
    private String sessionID;

    /**
     * myGCC ASPXAUTH cookie.
     */
    private String aspxauth;

    /**
     * myGCC browser refresh token.
     */
    private String browserRefresh;

    /**
     * myGCC view state token.
     */
    private String viewstate;

    /**
     * Generated boundary token.
     */
    private String boundary;

    /**
     * Constructor from Token.
     * @param tok token
     */
    public Session(final Token tok) {
        setToken(tok);
        setBoundary();
    }

    /**
     * Default constructor.
     */
    private Session() {
    }

    /**
     * Create session by getting new session identifier.
     * @throws NetworkException error connection to myGCC
     * @throws InvalidCredentialsException invalid myGCC credentials
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final void createSession() throws NetworkException,
            InvalidCredentialsException, UnexpectedResponseException {
        requestSessionID();
        authenticateSession();
    }

    /**
     * Set boundary string.
     */
    private void setBoundary() {
        boundary = "---------------------------" + generateBoundary();
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
     * Gets a new sessionID from myGCC.
     * @throws NetworkException bad connection to my.gcc.edu
     */
    private void requestSessionID() throws NetworkException {
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
     * Authenticate session and get session data.
     * @throws InvalidCredentialsException username and password incorrect
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    private void authenticateSession() throws InvalidCredentialsException,
            UnexpectedResponseException {
        // Return previously retrieved ASPXAuth
        if (getASPXAuth() != null
                && getBrowserRefresh() != null
                && getViewstate() != null) {
            return;
        }

        LinkedHashMap<String, String> postValues = new
                LinkedHashMap<String, String>() { {
            put("_scriptManager_HiddenField", "");
            put("__EVENTTARGET", "");
            put("__EVENTARGUMENT", "");
            put("__VIEWSTATE", "");
            put("__VIEWSTATEGENERATOR", "");
            put("___BrowserRefresh", "");
            put("userName", token.getUsername());
            put("password", token.getPassword());
            put("btnLogin", "Login");
            put("ctl04$tbSearch", "Search...");
        } };

        String postData = postData(postValues, getBoundary());

        HttpURLConnection http = createPOST(BASEURL, new
                HashMap<String, String>() { {
                    put("Content-Type",
                            "multipart/form-data; boundary=" + getBoundary());
                    put("Cookie", "ASP.NET_SessionId=" + sessionID + ";");
                } }, postData);

        // Check if logged in
        List<String> cookies = http.getHeaderFields().get("Set-Cookie");

        // Should be a ASPXAUTH cookie and a session heartbeat
        if (cookies.size() != 2) {
            throw new InvalidCredentialsException("2 cookies expected; "
                    + cookies.size() + " received");
        }

        setASPXAuth(parseAspxauth(http.getHeaderFields().get("Set-Cookie")));

        try {
            String html = convertStreamToString(http.getInputStream());

            setViewstate(parseViewState(html));
            setBrowserRefresh(parseBrowserRefresh(html));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get session ID.
     * @return session ID.
     */
    public final String getSessionID() {
        return sessionID;
    }

    /**
     * Set session ID.
     * @param sid session ID
     */
    public final void setSessionID(final String sid) {
        this.sessionID = sid;
    }

    /**
     * Get ASPXAuth.
     * @return ASPXAuth
     */
    public final String getASPXAuth() {
        return aspxauth;
    }

    /**
     * Set ASPXAuth.
     * @param aa ASXPAuth
     */
    public final void setASPXAuth(final String aa) {
        this.aspxauth = aa;
    }

    /**
     * Get browser refresh.
     * @return browser refresh
     */
    public final String getBrowserRefresh() {
        return browserRefresh;
    }

    /**
     * Set browser refresh.
     * @param br browser refresh
     */
    public final void setBrowserRefresh(final String br) {
        this.browserRefresh = br;
    }

    /**
     * Get view state.
     * @return view state
     */
    public final String getViewstate() {
        return viewstate;
    }

    /**
     * Set view state.
     * @param vs viewstate
     */
    public final void setViewstate(final String vs) {
        this.viewstate = vs;
    }

    /**
     * Get boundary string.
     * @return boundary string
     */
    public final String getBoundary() {
        return boundary;
    }

    /**
     * Get Token.
     * @return Token object
     */
    public final Token getToken() {
        return token;
    }

    /**
     * Set token.
     * @param tok Token object
     */
    public final void setToken(final Token tok) {
        this.token = tok;
    }
}
