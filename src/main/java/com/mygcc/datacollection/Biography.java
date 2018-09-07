package com.mygcc.datacollection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Biography class.
 *
 * The Biography class is used to get data about the authenticated user from
 * myGCC.
 */
public class Biography extends MyGCCDataCollection {
    /**
     * myGCC About Me URL.
     */
    private static final String ABOUTURL =
            "https://my.gcc.edu/ICS/?tool=myProfileSettings";

    /**
     * myGCC Contact Information URL.
     */
    private static final String CONTACTURL =
            "https://my.gcc.edu/ICS/?tool=myProfileSettings&screen="
                    + "ContactInformationView";

    /**
     * myGCC Academic Information URL.
     */
    private static final String ACADEMICURL =
            "https://my.gcc.edu/ICS/?tool=myProfileSettings&screen="
                    + "AcademicInformationView";

    /**
     * Enumeration of Biography Info page HTML selectors for relevant data.
     */
    private enum BiographyID {
        /**
         * Degree.
         */
        DEGREE("#CP_V_AcademicInformationCards_ctl00_AcademicInformationCard_"
                + "InformationSetsRepeater_ctl00_InformationItemsRepeater_"
                + "ctl00_Value"),

        /**
         * Major.
         */
        MAJOR("#CP_V_AcademicInformationCards_ctl00_AcademicInformationCard_"
                + "InformationSetsRepeater_ctl00_InformationItemsRepeater_"
                + "ctl00_Value"),

        /**
         * First name.
         */
        FIRSTNAME("#CP_V_CampusName"),

        /**
         * Middle name.
         */
        MIDDLENAME("#CP_V_MiddleName"),

        /**
         * Last name.
         */
        LASTNAME("#CP_V_LastName"),

        /**
         * Email.
         */
        EMAIL("#emailTable tbody tr td"),

        /**
         * Birth date.
         */
        BIRTHDATE("#CP_V_DateOfBirth"),

        /**
         * Marital status.
         */
        MARITALSTAT("#CP_V_MaritalStatus option"),

        /**
         * Gender.
         */
        GENDER("#CP_V_Gender option"),

        /**
         * Ethnicity.
         */
        ETHNICITY("#CP_V_Ethnicity option"),

        /**
         * ID number.
         */
        IDNUMBER("#CP_V_ViewHeader_SiteManagerLabel");

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

        String aspxauth = auth.getASPXAuth();
        String seshid = auth.getSessionID();

        try {
            String aboutHTML = getHTMLData(ABOUTURL, seshid, aspxauth);
            String contactHTML = getHTMLData(CONTACTURL, seshid, aspxauth);
            String academicHTML = getHTMLData(ACADEMICURL, seshid, aspxauth);

            return getUserDataFromHTML(aboutHTML, contactHTML, academicHTML);
        } catch (IOException e) {
            System.out.println(e.getMessage());

            throw new UnexpectedResponseException(e.getMessage());
        }
    }

    /**
     * Gets string of HTML from the given URL.
     * @param url url to retrieve
     * @param seshid session ID
     * @param aspxauth ASPX auth token
     * @return string of HTML
     * @throws IOException if the data can't be retrieved
     */
    public String getHTMLData(final String url,
                              final String seshid,
                              final String aspxauth)
            throws IOException {
        URLConnection conn = new URL(url).openConnection();

        conn.setRequestProperty("Cookie", "ASP.NET_SessionId=" + seshid
                + "; .ASPXAUTH=" + aspxauth);

        // Get HTML data from request
        BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "UTF-8"));

        // Get HTML data
        String htmldata = in.lines().collect(Collectors.joining());

        // Close input stream
        in.close();

        return htmldata;
    }

    /**
     * Parse HTML and get user data.
     *
     * @param aboutMeHtml String of HTML for the about me page
     * @param contactHTML String of HTML for the contact info page
     * @param academicHTML String of HTML for the academic info page
     * @return Map of data
     * @throws UnexpectedResponseException unexpected response from myGCC
     */
    private Map<String, String> getUserDataFromHTML(final String aboutMeHtml,
                                                    final String contactHTML,
                                                    final String academicHTML)
            throws UnexpectedResponseException {
        Document doc = Jsoup.parse(aboutMeHtml);
        String studentNameIDRaw = getDataFromHTML(doc,
                BiographyID.IDNUMBER.id());
        String studentName = trim(StringUtils.substringBetween(studentNameIDRaw,
                "My profile and settings - ", ","));
        String studentID = trim(StringUtils.substringAfter(studentNameIDRaw,
                "#"));

        String firstName = trim(getValueAttrFromHTML(doc,
                BiographyID.FIRSTNAME.id()));
        String middleName = trim(getValueAttrFromHTML(doc,
                BiographyID.MIDDLENAME.id()));
        String lastName = trim(getValueAttrFromHTML(doc,
                BiographyID.LASTNAME.id()));

        Document contactDoc = Jsoup.parse(contactHTML);
        String email = trim(contactDoc.select(BiographyID.EMAIL.id())
                .get(1).text());

        Document academicDoc = Jsoup.parse(academicHTML);

        String[] studentNameParts = studentName.split(" ");
        return new HashMap<String, String>() { {
            put("name", studentName);
            put("name_short", getShortName(firstName, lastName));
            put("name_long", getLongName(firstName, middleName, lastName));
            put("major", getDataFromHTML(academicDoc,
                    BiographyID.MAJOR.id()));
            put("degree", getDataFromHTML(academicDoc,
                    BiographyID.DEGREE.id()));
            put("email", email);
            put("birth", getValueAttrFromHTML(doc,
                    BiographyID.BIRTHDATE.id()));
            put("marital", getValueFromHTMLSelect(doc,
                    BiographyID.MARITALSTAT.id()));
            put("gender", getValueFromHTMLSelect(doc,
                    BiographyID.GENDER.id()));
            put("ethnicity", getValueFromHTMLSelect(doc,
                    BiographyID.ETHNICITY.id()));
            put("ID", studentID);
        } };
    }

    /**
     * Get the first and last name.
     *
     * Removes all names except the first and the last name.
     * @param studentName full name
     * @return first and last name separated by a space
     */
    private String getShortName(final String studentName) {
        String[] studentNameParts = studentName.split(" ");
        return studentNameParts[0] + " "
                + studentNameParts[studentNameParts.length - 1];
    }

    /**
     * Get the first and last name when each value can be reliably separated.
     * @param firstName first name
     * @param lastName last name
     * @return short name string
     */
    private String getShortName(final String firstName,
                                final String lastName) {
        return firstName + " " + lastName;
    }

    /**
     * Get the full name.
     *
     * Note: This is the same as the name field. Before Jenzabar v9,
     * name was returned in the form:
     * Mr. <Last Name>, <First Name> <Middle Name>
     *
     * @param studentName full name
     * @return full name
     */
    private String getLongName(final String studentName) {
        return studentName;
    }

    /**
     * Get the full name when each value can be reliably separated.
     * @param firstName first name
     * @param middleName middle name
     * @param lastName last name
     * @return full name
     */
    private String getLongName(final String firstName,
                               final String middleName,
                               final String lastName) {
        return firstName + " " + middleName + " " + lastName;
    }

    /**
     * Get selector value from HTML.
     * @param html document to parse
     * @param selector HTML selector
     * @return selected value
     */
    private String getDataFromHTML(final Document html,
                                   final String selector) {
        Elements name = html.select(selector);
        return trim(name.text());
    }

    /**
     * Get value attribute of input field from HTML.
     * @param html document to parse
     * @param selector HTML selector
     * @return selected value
     */
    private String getValueAttrFromHTML(final Document html,
                                        final String selector) {
        Elements name = html.select(selector);
        return trim(name.val());
    }

    /**
     * Get selected text value of option in HTML select element.
     * @param html document to parse
     * @param selector HTML selector
     * @return selected value or empty string
     */
    private String getValueFromHTMLSelect(final Document html,
                                          final String selector) {
        Elements selectElement = html.select(selector);
        for (Element el : selectElement) {
            if (el.hasAttr("selected")) {
                return trim(el.text());
            }
        }
        return "";
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
