package com.mygcc.datacollection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
 * Chapel class.
 *
 * The Chapel class is used to get Chapel data from myGCC.
 */
public class Chapel {
    /**
     * myGCC chapel URL.
     */
    private static final String MYCHAP =
            "https://my.gcc.edu/ICS/Student/Default_Page.jnz?"
                    + "portlet=Chapel_Attendance";

    /**
     * myGCC credentials authorization object.
     */
    private Session auth;

    /**
     * Chapel constructor.
     * @param token myGCC login credentials
     */
    public Chapel(final Token token) {
        this.auth = new Session(token);
    }

    /**
     * Get chapel data from myGCC.
     * @return The same map from {@link #getChapelDataFromTable(String)}.
     * @throws ExpiredSessionException myGCC session expired
     * @throws UnexpectedResponseException unexpected response from myGCC
     * @throws NetworkException bad connection to myGCC
     * @throws InvalidCredentialsException invalid myGCC credentials
     */
    public final Map<String, Integer> getChapelData() throws
            ExpiredSessionException, UnexpectedResponseException,
            NetworkException, InvalidCredentialsException {
        // Create session
        auth.createSession();

        String aspxauth = auth.getASPXAuth();
        String seshid = auth.getSessionID();
        try {
            //
            // Get URL to chapel page from myGCC.
            //

            URLConnection conn = new URL(MYCHAP).openConnection();

            conn.setRequestProperty("Cookie", "ASP.NET_SessionId=" + seshid
                    + "; .ASPXAUTH=" + aspxauth);

            // Get HTML data from request
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "UTF-8"));

            // Get HTML data
            String htmldata = in.lines().collect(Collectors.joining());

            // Close input stream
            in.close();

            String chapurl = getChapelUrlFromDocument(htmldata);

            // Replace spaces in URL with HTML %20 encoding
            String encchapurl = chapurl.replace(" ", "%20");

            //
            // Get data from chapel page URL.
            //

            // Get data from chapel URL
            URLConnection conn2 = new URL(encchapurl).openConnection();

            // Get HTML data from request
            BufferedReader in2 = new BufferedReader(new InputStreamReader(
                    conn2.getInputStream(), "UTF-8"));
            String data = in2.lines().collect(Collectors.joining());

            // Close input stream
            in2.close();

            return getChapelDataFromTable(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnexpectedResponseException("unknown IOException "
                    + "occurred");
        }
    }

    /**
     * Get chapel URL from HTML.
     * @param html string of HTML to parse
     * @return URL string
     * @throws ExpiredSessionException myGCC session expired
     */
    private String getChapelUrlFromDocument(final String html)
            throws ExpiredSessionException {
        Document doc = Jsoup.parse(html);
        Elements iframe = doc.select("#pg0_V_iframe");
        String url = iframe.attr("src");
        if (url == null || url.equals("")) {
            throw new ExpiredSessionException("Error finding src parameter");
        }
        return url;
    }

    /**
     * Gets a map of five parameters from HTML.
     * @param html string of HTML to parse
     * @return Map collection that contains type of chapel as a key
     *  and the number of those chapels as a value.
     * @throws UnexpectedResponseException HTML not formatted as expected
     */
    private Map<String, Integer> getChapelDataFromTable(final String html)
            throws UnexpectedResponseException {
        // Number of fields expected from chapel request
        final int expectedChapelDataLen = 13;

        // Indexes of specific data received from chapel response
        final int reqParamIndex = 8;
        final int makeuParamIndex = 9;
        final int attParamIndex = 10;
        final int remParamIndex = 11;
        final int specParamIndex = 12;

        Document doc = Jsoup.parse(html);
        Elements table = doc.select("#grd tbody");
        Elements tb = table.select("tr");
        String[] chapeldata = tb.text().split(" ");

        if (chapeldata.length != expectedChapelDataLen) {
            throw new UnexpectedResponseException("received data invalid: "
                    + "expected 13; " + chapeldata.length + " found");
        }

        Map<String, Integer> chdatamap = new HashMap<>();
        chdatamap.put("required", Integer.parseInt(chapeldata[reqParamIndex]));
        chdatamap.put("makeups", Integer.parseInt(chapeldata[makeuParamIndex]));
        chdatamap.put("attended", Integer.parseInt(chapeldata[attParamIndex]));
        chdatamap.put("remaining", Integer.parseInt(chapeldata[remParamIndex]));
        chdatamap.put("special", Integer.parseInt(chapeldata[specParamIndex]));
        return chdatamap;
    }
}
