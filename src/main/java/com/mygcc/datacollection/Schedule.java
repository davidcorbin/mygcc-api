package com.mygcc.datacollection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for getting schedule information.
 */
public class Schedule {

    /**
     * URL to get users' schedule.
     */
    private static final String MYSCH =
            "https://my.gcc.edu/ICS/Academics/Home.jnz"
                    + "?portlet=Student_Schedule";

    /**
     * Authorization object to get schedule.
     */
    private Session auth;

    /**
     * Default constructor.
     * @param token myGCC username and password.
     */
    public Schedule(final Token token) {
        this.auth = new Session(token);
    }

    /**
     * Get the class schedule of a user.
     * @return A list of the user's schedule.
     * @throws UnexpectedResponseException If the request goes wrong.
     * @throws NetworkException error connection to myGCC
     * @throws InvalidCredentialsException username or password incorrect
     */
    public final List<Object> getScheduleData()
            throws UnexpectedResponseException, NetworkException,
            InvalidCredentialsException {
        // Create session
        auth.createSession();

        String rawHTML = getContentFromUrl();
        List<Object> prettyJSON = parseScheduleHTML(rawHTML);
        return prettyJSON;
    }

    /**
     * Turns ugly HTML schedule into pretty JSON schedule.
     * @param raw The raw unformatted HTML from myGCC.
     * @return A JSON formatted class schedule.
     */
    private List<Object> parseScheduleHTML(final String raw) {
        final int creditNumber = 3;
        final int profNumber = 6;
        final int timeNumber = 7;
        final int locationNumber = 9;
        Document doc = Jsoup.parse(raw);
        Elements rows = doc.select(".gbody > tr:not(.subitem)");
        List<Object> classArray = new ArrayList<>();
        for (Element c : rows) {
            Map<String, Object> clas = new HashMap<>();
            clas.put("course", c.select("td").get(1).text());
            clas.put("code", c.select("td").get(1).text()
                    .replaceAll(" ", ""));
            clas.put("title", c.select("td").get(2).text());
            clas.put("credits", c.select("td").get(creditNumber).text());

            Elements profs = c.select("td").get(profNumber)
                    .select("ul > li");
            List<Object> profsList = new ArrayList<>();
            for (Element p : profs) {
                profsList.add(p.toString()
                        .replaceAll(" <br> &nbsp; </li>", "")
                        .replaceAll("<li> ", ""));
            }
            clas.put("professor", profsList);

            Elements times = c.select("td").get(timeNumber)
                    .select("ul > li");
            List<Object> timesList = new ArrayList<>();
            for (Element p : times) {
                String currentTimeString = p.toString()
                        .replaceAll(" </li>", "")
                        .replaceAll("<li> ", "")
                        .replaceAll("<br>", "")
                        .replaceAll("-", "")
                        .replaceAll("AM", "")
                        .replaceAll("PM", "");
                String[] stringparts = currentTimeString.split("\\s+");
                for (int i = 0; i < stringparts[0].length(); i++) {
                    Map<String, Object> currDay = new HashMap<>();
                    currDay.put("day", stringparts[0].charAt(i));
                    currDay.put("start", stringparts[1]);
                    currDay.put("end", stringparts[2]);
                    timesList.add(currDay);
                }


            }
            clas.put("times", timesList);

            Elements locations = c.select("td").get(locationNumber)
                    .select("ul > li");
            List<Object> locationsList = new ArrayList<>();
            for (Element p : locations) {
                locationsList.add(p.toString()
                        .replaceAll(" &nbsp; </li>", "")
                        .replaceAll("<li> ", "")
                        .replaceAll("<br>", "")
                        .replaceAll("/", "-")
                        .trim());
            }
            clas.put("location", locationsList);

            classArray.add(clas);
        }
        return classArray;
    }

    /**
     * Gets the raw HTML schedule from myGCC.
     * @return The raw HTML schedule.
     * @throws UnexpectedResponseException When internet has problems.
     */
    private String getContentFromUrl()
            throws UnexpectedResponseException {
        try {
            URL gccUrl = new URL(MYSCH);
            HttpURLConnection http = (HttpURLConnection) gccUrl
                    .openConnection();
            http.setRequestProperty("Cookie",
                    "ASP.NET_SessionId=" + auth.getSessionID());
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
