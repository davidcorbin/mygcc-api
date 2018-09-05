package com.mygcc.datacollection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * ClassData class is a helper class for getting data about classes from myGCC.
 */
public abstract class ClassData extends MyGCCDataCollection {
    /**
     * This number's significance is unknown.
     * It must be included in the URL however.
     */
    public static final String MAGIC_URL_NUMBER = "10";

    /**
     * Current Year.
     */
    public static final String CURRENT_YEAR = "2018";

    /**
     * Current Year (according to MyGCC).
     */
    public static final String MYGCC_YEAR = "2018";

    /**
     * The expected number of course code sections.
     */
    public static final int EXPECTED_CC_LENGTH = 3;

    /**
     * URL to get users' homework.
     */
    public static final String MYCON = BASEURL + "/ICS/Academics";

    /**
     * Authorization object to get homework.
     */
    private Session auth;

    /**
     * The course code of the class to get homework from.
     */
    private String ccode;

    /**
     * Get data and return as List for API.
     * @return List of data from myGCC
     * @throws UnexpectedResponseException If the request goes wrong
     * @throws InvalidCredentialsException If credentials are invalid
     * @throws NetworkException If network is down
     * @throws ClassDoesNotExistException If a class does not exist
     * @throws StudentNotInClassException If the student is not enrolled
     *      in the specified class
     */
    public abstract Map<String, Object> getData() throws
            UnexpectedResponseException, InvalidCredentialsException,
            NetworkException, ClassDoesNotExistException,
            StudentNotInClassException;

    /**
     * Get session.
     * @return session
     */
    public final Session getAuth() {
        return auth;
    }

    /**
     * Set session.
     * @param authObj session
     */
    public final void setAuth(final Session authObj) {
        this.auth = authObj;
    }

    /**
     * Get course code.
     * @return course code
     */
    public final String getCcode() {
        return ccode;
    }

    /**
     * Set course code.
     * @param courseCode course code
     */
    public final void setCcode(final String courseCode) {
        this.ccode = courseCode;
    }

    /**
     * Remove all non-alphanumeric characters.
     * @param courseCode raw course code
     * @return sanitized course code
     */
    public final String sanitizeCourseCode(final String courseCode) {
        return courseCode.replaceAll("[^A-Za-z0-9]", "");
    }

    /**
     * Convert course code to URL.
     * @param courseCode course code
     * @return URL to course
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final String courseCodeToURL(final String courseCode)
            throws ClassDoesNotExistException {
        String[] parts = courseCode.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        if (parts.length != EXPECTED_CC_LENGTH) {
            throw new ClassDoesNotExistException();
        }
        String subject = parts[0];
        String number = parts[1];
        String section = parts[2];
        StringBuilder url = new StringBuilder();
        url.append(MYCON);
        url.append(String.format("/%s/%s_%s", subject, subject, number));
        if (section.length() > 1) {
            url.append(String.format("/%s_%s-%s_%s-%s____L/",
                    MYGCC_YEAR, MAGIC_URL_NUMBER, subject,
                    number, section.charAt(0)));
        } else {
            url.append(String.format("/%s_%s-%s_%s-%s/",
                    MYGCC_YEAR, MAGIC_URL_NUMBER, subject, number, section));
        }
        return url.toString();
    }

    /**
     * Get coursework url for a given class.
     * @param courseCode myGCC course code
     * @return URL to coursework page
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final String getCourseworkURL(final String courseCode)
            throws ClassDoesNotExistException {
        return courseCodeToURL(courseCode) + "Coursework.jnz";
    }

    /**
     * Get course information url for a given class.
     * @param courseCode myGCC course code
     * @return URL to course information page
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final String getCourseInfoURL(final String courseCode)
            throws ClassDoesNotExistException {
        return courseCodeToURL(courseCode) + "Course_Information.jnz";
    }

    /**
     * Get collaboration url for a given class.
     * @param courseCode myGCC course code
     * @return URL to collaboration page
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final String getCollaborationURL(final String courseCode)
            throws ClassDoesNotExistException {
        return courseCodeToURL(courseCode)
                + "Collaboration.jnz?portlet=Coursemates";
    }

    /**
     * Get files url for a given class.
     * @param courseCode myGCC course code
     * @return URL to files page
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    public final String getFilesURL(final String courseCode)
            throws ClassDoesNotExistException {
        return courseCodeToURL(courseCode)
                + "Main_Page.jnz?portlet=Handouts&screen=MainView"
                + "&screenType=next&viewType=Card";
    }

    /**
     * Gets the raw HTML from myGCC.
     * @param url The url to get content from.
     * @return The raw HTML schedule.
     * @throws UnexpectedResponseException When internet has problems.
     */
    public final String getContentFromUrl(final String url)
            throws UnexpectedResponseException {
        try {
            URL gccUrl = new URL(url);
            HttpURLConnection http = (HttpURLConnection) gccUrl
                    .openConnection();
            http.setRequestProperty("Cookie",
                    "ASP.NET_SessionId=" + getAuth().getSessionID()
                            + "; .ASPXAUTH=" + getAuth().getASPXAuth());
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    http.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder output = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                output.append(inputLine);
            }
            in.close();
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnexpectedResponseException("unknown IOException "
                    + "occurred");
        }
    }
}
