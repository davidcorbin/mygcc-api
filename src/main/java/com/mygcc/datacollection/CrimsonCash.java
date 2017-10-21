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
 * Crimson Cash class.
 *
 * The Crimson Cash class is used to get Crimson Cash data from myGCC.
 */
public class CrimsonCash {
    /**
     * myGCC Crimson Cash URL.
     */
    private static final String MYCASH =
            "https://my.gcc.edu/ICS/Financial_Info/Default_Page.jnz?"
                    + "portlet=Universal_Portlet";

    /**
     * myGCC credentials authorization object.
     */
    private Authorization auth;

    /**
     * Crimson Cash constructor.
     * @param authorization myGCC login credentials
     */
    public CrimsonCash(final Authorization authorization) {
        this.auth = authorization;
    }

    /**
     * Get Crimson Cash data from myGCC.
     * @return Map with Crimson Cash data
     * @throws ExpiredSessionException myGCC session expired
     * @throws UnexpectedResponseException unexpected response from myGCC
     * @throws NetworkException bad connection to myGCC
     * @throws InvalidCredentialsException invalid myGCC credentials
     */
    public final Map<String, Object> getCrimsonCashData() throws
            ExpiredSessionException, UnexpectedResponseException,
            NetworkException, InvalidCredentialsException {
        String aspxauth = auth.getASPXAuth();
        String seshid = auth.getSessionID();
        try {
            //
            // Get URL to Crimson Cash page from myGCC.
            //

            URLConnection conn = new URL(MYCASH).openConnection();

            conn.setRequestProperty("Cookie", "ASP.NET_SessionId=" + seshid
                    + "; .ASPXAUTH=" + aspxauth);

            // Get HTML data from request
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String htmldata = in.lines().collect(Collectors.joining());
            String cashurl = getCrimsonCashUrlFromDocument(htmldata);

            // Replace spaces in URL with HTML %20 encoding
            String enccashurl = cashurl.replace(" ", "%20");

            //
            // Get data from Crimson Cash page URL.
            //

            // Get data from Crimson Cash URL
            URLConnection conn2 = new URL(enccashurl).openConnection();

            // Get HTML data from request
            BufferedReader in2 = new BufferedReader(new InputStreamReader(
                    conn2.getInputStream()));
            String data = in2.lines().collect(Collectors.joining());

            return getCrimsonCashDataFromTable(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnexpectedResponseException("unknown IOException "
                    + "occurred");
        }
    }

    /**
     * Get Crimson Cash URL from HTML.
     * @param html string of HTML to parse
     * @return URL string
     * @throws ExpiredSessionException myGCC session expired
     */
    private String getCrimsonCashUrlFromDocument(final String html)
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
     * Gets a map of one parameters from HTML.
     * @param html string of HTML to parse
     * @return Map 5 parameters
     * @throws UnexpectedResponseException HTML not formatted as expected
     */
    private Map<String, Object> getCrimsonCashDataFromTable(final String html)
            throws UnexpectedResponseException {
        // Indexes of specific data received from chapel response

        Document doc = Jsoup.parse(html);
        String balance = doc.select("#form1 > div")
                .get(0)
                .select("span > b")
                .text()
                .replace("$", "");

        Map<String, Object> ccdatamap = new HashMap<>();
        ccdatamap.put("balance", Float.parseFloat(balance));
        return ccdatamap;
    }
}
