package com.mygcc.datacollection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Tools for data collection.
 */
public abstract class MyGCCDataCollection {
    /**
     * Base myGCC url.
     */
    public static final String BASEURL = "https://my.gcc.edu";

    /**
     * Convert InputStream to String.
     * @param is InputStream to convert
     * @return String of InputStream data
     */
    public static String convertStreamToString(final InputStream is) {
        Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
        if (s.hasNext()) {
            return s.next();
        } else {
            return "";
        }
    }

    /**
     * Get browserrefresh hidden input value.
     * @param html html to parse
     * @return browserrefresh value
     */
    public static String parseBrowserRefresh(final String html) {
        Document doc = Jsoup.parse(html);
        Elements name = doc.select("input[name=___BrowserRefresh]");
        return name.attr("value");
    }

    /**
     * Get VIEWSTATE hidden input value.
     * @param html html to parse
     * @return VIEWSTATE value
     */
    public static String parseViewState(final String html) {
        Document doc = Jsoup.parse(html);
        Elements name = doc.select("input[name=__VIEWSTATE]");
        return name.attr("value");
    }

    /**
     * Get ASPXAuth from list of cookies.
     * @param cookies list of cookies
     * @return ASPXAuth string
     */
    public static String parseAspxauth(final List<String> cookies) {
        String aspRaw = cookies.get(0);

        // Trim everything but the cookie value itself
        return StringUtils.substringBetween(aspRaw,
                ".ASPXAUTH=",
                "; path=/");
    }

    /**
     * Create HTTP POST data string.
     * @param data map in the form name: value
     * @param boundary boundary string
     * @return POST data
     */
    public static String postData(final Map<String, String> data,
                                  final String boundary) {
        StringBuilder separator = new StringBuilder()
                .append("--")
                .append(boundary)
                .append("\n");
        StringBuilder postData = new StringBuilder();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            // Boundary
            postData.append(separator);

            // Content disposition line
            postData.append("Content-Disposition: form-data; name=\"")
                    .append(name)
                    .append("\"\n");
            // Empty line between name and value
            postData.append("\n");

            // Value
            postData.append(value)
                    .append("\n");
        }

        // Closing boundary
        postData.append("--")
                .append(boundary)
                .append("--\n");

        return postData.toString();
    }

    /**
     * Create HTTP POST request object.
     * @param url url for POST request
     * @param requestProperties HTTP request properties
     * @param postData data to be sent
     * @return HttpURLConnection
     * @throws NetworkException error connection to URL
     */
    public static HttpURLConnection createPOST(final String url,
                                               final Map<String, String>
                                                       requestProperties,
                                               final String postData)
            throws NetworkException {
        URLConnection con;
        try {
            con = new URL(url).openConnection();
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Malformed url", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unknown IOException");
        }
        HttpURLConnection http = (HttpURLConnection) con;
        try {
            http.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new IllegalStateException("Unknown protocol exception");
        }
        http.setDoOutput(true);
        http.setInstanceFollowRedirects(false);

        // Set request properties
        for (Map.Entry<String, String> prop : requestProperties.entrySet()) {
            http.setRequestProperty(prop.getKey(), prop.getValue());
        }

        DataOutputStream wr;
        try {
            wr = new DataOutputStream(http.getOutputStream());
            wr.writeBytes(postData);
            wr.close();
        } catch (IOException e) {
            throw new NetworkException("Could not connect to myGCC");
        }

        return http;
    }

    /**
     * Convert relative URL to absolute URL.
     * @param relURL relative URL string
     * @return absolute URL string
     */
    public final String relToAbsURL(final String relURL) {
        return BASEURL + relURL.trim();
    }
}
