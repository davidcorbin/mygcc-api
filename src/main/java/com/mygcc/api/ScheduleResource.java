package com.mygcc.api;

import com.mygcc.datacollection.Authorization;

import com.mygcc.datacollection.ExpiredSessionException;
import com.mygcc.datacollection.InvalidCredentialsException;
import com.mygcc.datacollection.NetworkException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for getting schedule information.
 */
@Path("/1/user")
public class ScheduleResource {

    /**
     * Main response for schedule.
     * @param user Username (temporary)
     * @param pass Password (temporary)
     * @param token Authorization Token (future)
     * @return Response of JSON with schedule information.
     * @throws InvalidCredentialsException When invalid credentials are used.
     * @throws ExpiredSessionException When session is expired.
     * @throws NetworkException When communication with myGCC is interrupted.
     * @throws IOException When IO functions don't function properly.
     */
    @GET
    @Path("/schedule")
    @Produces(MediaType.APPLICATION_JSON)
    public final Response scheduleResponse(
            @HeaderParam("user") final String user,
            @HeaderParam("pass") final String pass,
            @HeaderParam("Authorization") final String token)
    throws InvalidCredentialsException,
            ExpiredSessionException,
            NetworkException,
            IOException {

        if (token == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("date", Instant.now().getEpochSecond());
            response.put("message",
                    "Authorization header empty or does not exist.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }

        System.out.println(user);
        System.out.println(pass);
        Authorization userAuth = new Authorization(user, pass);
        userAuth.setSessionID();
        userAuth.getASPXAuth();
        String sessionId = userAuth.getSessionID();
        System.out.println(sessionId);
        String rawHTML = getContentFromUrl(sessionId);

        List<Object> prettyJSON = parseScheduleHTML(rawHTML);

        return Response.status(Response.Status.OK)
                .entity(prettyJSON)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();

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
        Map<String, Object> classes = new HashMap<>();
        for (Element c : rows) {
            Map<String, Object> clas = new HashMap<>();
            clas.put("course", c.select("td").get(1).text());
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
     * @param session The sessionID cookie, to authenticate with myGCC.
     * @return The raw HTML schedule.
     * @throws IOException When internet has problems.
     */
    private String getContentFromUrl(final String session)
            throws IOException {
        URL gccUrl = new URL("https://my.gcc.edu/ICS/Academics/Home.jnz"
                + "?portlet=Student_Schedule");
        URLConnection con = gccUrl.openConnection();
        HttpURLConnection http = (HttpURLConnection) con;
        http.setRequestProperty("Cookie",
                "ASP.NET_SessionId=" + session);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                http.getInputStream()));
        String inputLine;
        StringBuilder output = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            output.append(inputLine);
        }
        in.close();
        return output.toString();
    }
}
