package com.mygcc.datacollection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Chapel class.
 *
 * The Chapel class is used to get Chapel data from myGCC.
 */
public class Biography extends MyGCCDataCollection {
    /**
     * myGCC chapel URL.
     */
    private static final String URL = "https://my.gcc.edu/ICS/";

    /**
     * Enumeration of Biography Info page HTML selectors for relevant data.
     */
    private enum BiographyID {
        /**
         * Degree.
         */
        DEGREE("#CP_V_rptEducation_ctl00_lblDegreeDesc"),

        /**
         * Major.
         */
        MAJOR("#CP_V_rptEducation_ctl00_lblMajorDesc"),

        /**
         * Name.
         */
        NAME("#CP_V_lblUserName"),

        /**
         * Email.
         */
        EMAIL("#CP_V_divOtherAddresses table tbody tr:eq(0) td:eq(1)"),

        /**
         * Birth date.
         */
        BIRTHDATE("#CP_V_txtBirthdate"),

        /**
         * Marital status.
         */
        MARITALSTAT("#CP_V_txtMaritalStatus"),

        /**
         * Gender.
         */
        GENDER("#CP_V_txtGender"),

        /**
         * Ethnicity.
         */
        ETHNICITY("#CP_V_txtEthnicity"),

        /**
         * ID number.
         */
        IDNUMBER("#CP_V_txtIDNumber");

        /**
         * Identifier.
         */
        private String id;

        /**
         * Set identifier.
         * @param ident identifier
         */
        BiographyID(final String ident) {
            this.id = ident;
        }

        /**
         * Get identifier.
         * @return identifier
         */
        public String id() {
            return id;
        }
    }

    /**
     * myGCC credentials authorization object.
     */
    private Session auth;

    /**
     * Chapel constructor.
     * @param token myGCC login credentials
     */
    public Biography(final Token token) {
        this.auth = new Session(token);
    }

    /**
     * Get client personal information from myGCC.
     * @return Map with chapel data
     * @throws ExpiredSessionException myGCC session expired
     * @throws UnexpectedResponseException unexpected response from myGCC
     * @throws NetworkException bad connection to myGCC
     * @throws InvalidCredentialsException invalid myGCC credentials
     */
    public final Map<String, String> getData() throws
            ExpiredSessionException, UnexpectedResponseException,
            NetworkException, InvalidCredentialsException {
        // Create session
        auth.createSession();

        try {
            // Make first request to open the personal info site.
            LinkedHashMap<String, String> postValues = new
                    LinkedHashMap<String, String>() { {
                put("_scriptManager_HiddenField", "");
                put("__EVENTTARGET", "welcomeBackBar");
                put("__EVENTARGUMENT", "accountInfo");
                put("__VIEWSTATE", auth.getViewstate());
                put("__VIEWSTATEGENERATOR", "38ABEAAB");
                put("___BrowserRefresh", auth.getBrowserRefresh());
                put("ctl04$tbSearch", "Search...");
                put("CP$V$PreferredName", "");
                put("CP$V$HideMiddleName", "on");
                put("CP$V$Prefix", "");
                put("CP$V$Suffix", "");
            } };

            String postData = postData(postValues, auth.getBoundary());

            HttpURLConnection http = createPOST(URL,
                    new HashMap<String, String>() { {
                        put("Content-Type",
                                "multipart/form-data; boundary="
                                        + auth.getBoundary());
                        put("Cookie",
                                "ASP.NET_SessionId=" + auth.getSessionID()
                                        + "; .ASPXAUTH=" + auth.getASPXAuth());
                        put("Accept", "text/html,application/xhtml+xml,"
                                + "application/xml;q=0.9,*/*;q=0.8");
                    } }, postData);

            String html = convertStreamToString(http.getInputStream());
            auth.setBrowserRefresh(parseBrowserRefresh(html));
            auth.setViewstate(parseViewState(html));

            // Make second request to open the biography info tab.
            LinkedHashMap<String, String> postValues2 = new
                    LinkedHashMap<String, String>() { {
                        put("_scriptManager_HiddenField", "");
                        put("__EVENTTARGET", "CP$t6");
                        put("__EVENTARGUMENT", "Biography View");
                        put("__VIEWSTATE", auth.getViewstate());
                        put("__VIEWSTATEGENERATOR", "38ABEAAB");
                        put("___BrowserRefresh", auth.getBrowserRefresh());
                        put("ctl04$tbSearch", "Search...");
                        put("CP$V$PreferredName", "");
                        put("CP$V$HideMiddleName", "on");
                        put("CP$V$Prefix", "");
                        put("CP$V$Suffix", "");
                    } };

            String postData2 = postData(postValues2, auth.getBoundary());

            HttpURLConnection http2 = createPOST(URL,
                    new HashMap<String, String>() { {
                        put("Content-Type",
                                "multipart/form-data; boundary="
                                        + auth.getBoundary());
                        put("Cookie",
                                "ASP.NET_SessionId=" + auth.getSessionID()
                                        + "; .ASPXAUTH=" + auth.getASPXAuth());
                        put("Accept", "text/html,application/xhtml+xml,"
                                + "application/xml;q=0.9,*/*;q=0.8");
                    } }, postData2);

            String bioHTML = convertStreamToString(http2.getInputStream());

            return getUserDataFromHTML(bioHTML);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnexpectedResponseException("unknown IOException "
                    + "occurred");
        }
    }

    /**
     * Parse HTML and get user data.
     * @param html data
     * @return Map of data
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    private Map<String, String> getUserDataFromHTML(final String html) throws
            UnexpectedResponseException {
        Document doc = Jsoup.parse(html);
        return new HashMap<String, String>() { {
            put("name", getDataFromHTML(doc, BiographyID.NAME.id()));
            put("major", getDataFromHTML(doc, BiographyID.MAJOR.id()));
            put("degree", getDataFromHTML(doc, BiographyID.DEGREE.id()));
            put("email", getDataFromHTML(doc, BiographyID.EMAIL.id()));
            put("birth", getDataFromHTML(doc, BiographyID.BIRTHDATE.id()));
            put("marital", getDataFromHTML(doc, BiographyID.MARITALSTAT.id()));
            put("gender", getDataFromHTML(doc, BiographyID.GENDER.id()));
            put("ethnicity", getDataFromHTML(doc, BiographyID.ETHNICITY.id()));
            put("ID", getDataFromHTML(doc, BiographyID.IDNUMBER.id()));
        } };
    }

    /**
     * Get selector value from HTML.
     * @param html document to parse
     * @param selector HTML selector
     * @return selected value
     */
    private String getDataFromHTML(final Document html, final String selector) {
        Elements name = html.select(selector);
        return trim(name.text());
    }

    /**
     * Trim space characters and non-breaking spaces.
     * @param value string to trim
     * @return trimmed string
     */
    private String trim(final String value) {
        return value.trim().replaceAll("(^\\h*)|(\\h*$)", "");
    }
}
