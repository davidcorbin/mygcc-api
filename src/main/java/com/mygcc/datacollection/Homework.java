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
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for getting homework information.
 */
public class Homework extends MyGCCDataCollection {

    /**
     * This number's significance is unknown.
     * It must be included in the URL however.
     *
     * There may be a pattern:
     * F17=10
     * S18=30
     */
    private static final String MAGIC_URL_NUMBER = "30";

    /**
     * Current Year, not sure how this changes so for now it is a variable.
     * Note: It is still '2017' for the spring 2018 semester.
     */
    private static final String CURRENT_YEAR = "2017";

    /**
     * Base myGCC url.
     */
    private static final String BASEURL = "https://my.gcc.edu";

    /**
     * URL to get users' homework.
     */
    private static final String MYCON = BASEURL + "/ICS/Academics";

    /**
     * Authorization object to get homework.
     */
    private Session auth;

    /**
     * The course code of the class to get homework from.
     */
    private String ccode;

    /**
     * The expected number of course code sections.
     */
    private static final int EXPECTED_CC_LENGTH = 3;

    /**
     * Default constructor.
     * @param token The auth token.
     * @param courseCode The course code of the specific class.
     */
    public Homework(final Token token, final String courseCode) {
        this.auth = new Session(token);
        this.ccode = sanitizeCourseCode(courseCode);
    }

    /**
     * Get the homework of a user's class.
     * @return A list of homework for that user's class.
     * @throws UnexpectedResponseException If the request goes wrong.
     * @throws InvalidCredentialsException If credentials are invalid.
     * @throws NetworkException If network is down.
     * @throws ClassDoesNotExistException If a class does not exist.
     * @throws StudentNotInClassException If the student is not enrolled
     *      in the specified class.
     */
    public final List<Object> getHomeworkData()
            throws UnexpectedResponseException,
            InvalidCredentialsException,
            NetworkException,
            ClassDoesNotExistException,
            StudentNotInClassException {
        auth.createSession();
        String hwURL = courseCodeToURL(ccode);
        String rawHTML = getContentFromUrl(hwURL);
        return parseHomeworkHTML(rawHTML);
    }

    /**
     * Turn a course code into the right url to get homework.
     * @param courseCode Should be in the form of CCCCdddC,
     *                   Where C is a letter and d is a number,
     *                   the first four letters should be the class subject,
     *                   the three letters should be the class number,
     *                   the last letter should be the class section.
     * @return A proper URL for homework of a class.
     * @throws UnexpectedResponseException If the course code is malformed.
     */
    private String courseCodeToURL(final String courseCode)
            throws UnexpectedResponseException {
        String[] parts = courseCode.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        if (parts.length != EXPECTED_CC_LENGTH) {
            throw new UnexpectedResponseException();
        }
        String subject = parts[0];
        String number = parts[1];
        String section = parts[2];
        StringBuilder url = new StringBuilder();
        url.append(MYCON);
        url.append(String.format("/%s/%s_%s", subject, subject, number));
        if (section.length() > 1) {
            url.append(String.format("/%s_%s-%s_%s-%s____L/Coursework.jnz",
                    CURRENT_YEAR, MAGIC_URL_NUMBER, subject,
                    number, section.charAt(0)));
        } else {
            url.append(String.format("/%s_%s-%s_%s-%s/Coursework.jnz",
                    CURRENT_YEAR, MAGIC_URL_NUMBER, subject, number, section));
        }
        return url.toString();
    }

    /**
     * Turns ugly HTML homework page into pretty JSON homework list.
     * @param raw The raw unformatted HTML from myGCC.
     * @return A JSON formatted homework.
     * @throws ClassDoesNotExistException If the class does not exist.
     * @throws StudentNotInClassException If the student is not in a class.
     */
    private List<Object> parseHomeworkHTML(final String raw)
            throws ClassDoesNotExistException, StudentNotInClassException {
        final int gradestringlength = 4;
        final int percentindex = 3;
        Map<String, Object> mainArray = new HashMap<>();
        Document doc = Jsoup.parse(raw);
        Elements notFound = doc.select(".notFound");
        if (notFound.text().contains("require you to be")) {
            throw new ClassDoesNotExistException();
        } else if (notFound.text().contains("permissions to view")) {
            throw new StudentNotInClassException();
        }
        Elements sections = doc.select(
                "#pg0_V__assignmentView__updatePanel > div.assignmentTitle");
        List<Object> classArray = new ArrayList<>();
        for (Element c : sections) {
            List<Object> sectionArray = new ArrayList<>();
            Elements assignments = c.nextElementSibling()
                    .select(".assignmentDisplay");
            for (Element d : assignments) {
                Map<String, Object> assignmentArray = new HashMap<>();

                // Get assignment title
                assignmentArray.put("title",
                        d.select("div.assignmentText > a").text());

                // Get assignment URL
                assignmentArray.put("assignment_url",
                        BASEURL + d.select("div.assignmentText > a")
                        .attr("href"));

                Map<String, Object> gradeArray = new HashMap<>();
                String[] gradeString = d.select("div.assignmentText > span")
                        .text().replaceAll("\\(|\\)|\\/|,", " ").split(" ");
                gradeString = Arrays.stream(gradeString)
                        .filter(s -> (s != null && s.length() > 0))
                        .toArray(String[]::new);
                gradeArray.put("received", gradeString[0]);
                if (gradeString.length > 1) {
                    gradeArray.put("points", gradeString[1]);
                    if (gradeString.length >= gradestringlength) {
                        gradeArray.put("letter", gradeString[2]);
                        gradeArray.put("percent", gradeString[percentindex]
                                .replaceAll("%", ""));
                    } else {
                        gradeArray.put("percent", gradeString[2]
                                .replaceAll("%", ""));
                    }
                }

                assignmentArray.put("grade", gradeArray);
                String parsedDate = dateStringToDate(
                        d.select("div.assignmentDue > strong").text());
                assignmentArray.put("due", parsedDate);
                assignmentArray.put("description",
                        d.select("div.assignmentDescription").text());
                assignmentArray.put("open", d.hasClass("open"));

                // Add class URL to assignment
                try {
                    assignmentArray.put("course_url", courseCodeToURL(ccode));
                } catch (UnexpectedResponseException e) {
                    throw new IllegalStateException("Unable to get URL");
                }

                sectionArray.add(assignmentArray);
            }
            mainArray.put(c.text(), sectionArray);
        }
        classArray.add(mainArray);
        return classArray;
    }

    /**
     * Converts A string with the date to proper ISO 8601 format.
     * @param rawDate The date string from myGCC
     * @return The date as an ISO 8601 compliant string.
     */
    private String dateStringToDate(final String rawDate) {
        final int timeindex = 4;
        final int timeindicatorindex = 5;
        final int timedifference = 12;
        String[] months = {"January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November",
                "December"};
        if (rawDate.length() <= 0) {
            return "";
        }
        String[] rawParts = rawDate.split(" ");
        int pMonth = Arrays.asList(months).indexOf(rawParts[1]) + 1;
        String pDay = rawParts[2];
        int pHour = Integer.parseInt(rawParts[timeindex].split(":")[0]);
        int pMinute = Integer.parseInt(rawParts[timeindex].split(":")[1]);
        if (rawParts[timeindicatorindex].equals("AM")
                && pHour == timedifference) {
            pHour = 0;
        }
        if (rawParts[timeindicatorindex].equals("PM")) {
            pHour += timedifference;
        }
        return String.format("%s-%02d-%sT%02d:%02d:00Z",
                CURRENT_YEAR, pMonth, pDay, pHour, pMinute);
    }

    /**
     * Gets the raw HTML schedule from myGCC.
     * @param url The url to get content from.
     * @return The raw HTML schedule.
     * @throws UnexpectedResponseException When internet has problems.
     */
    private String getContentFromUrl(final String url)
            throws UnexpectedResponseException {
        try {
            URL gccUrl = new URL(url);
            HttpURLConnection http = (HttpURLConnection) gccUrl
                    .openConnection();
            http.setRequestProperty("Cookie",
                    "ASP.NET_SessionId=" + auth.getSessionID()
                            + "; .ASPXAUTH=" + auth.getASPXAuth());
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

    /**
     * Remove all non-alphanumeric characters.
     * @param courseCode raw course code
     * @return sanitized course code
     */
    private String sanitizeCourseCode(final String courseCode) {
        return courseCode.replaceAll("[^A-Za-z0-9]", "");
    }
}
