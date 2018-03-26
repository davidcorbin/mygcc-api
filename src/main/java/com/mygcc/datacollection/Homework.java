package com.mygcc.datacollection;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for getting homework information.
 */
public class Homework extends ClassData {
    /**
     * Default constructor.
     * @param token The auth token.
     * @param courseCode The course code of the specific class.
     */
    public Homework(final Token token, final String courseCode) {
        setAuth(new Session(token));
        setCcode(sanitizeCourseCode(courseCode));
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
    public final Map<String, Object> getData() throws
            UnexpectedResponseException, InvalidCredentialsException,
            NetworkException, ClassDoesNotExistException,
            StudentNotInClassException {
        getAuth().createSession();
        String hwURL = getCourseworkURL(getCcode());
        String rawHTML = getContentFromUrl(hwURL);
        return parseHomeworkHTML(rawHTML);
    }

    /**
     * Turns ugly HTML homework page into pretty JSON homework list.
     * @param raw The raw unformatted HTML from myGCC.
     * @return A JSON formatted homework.
     * @throws ClassDoesNotExistException If the class does not exist.
     * @throws StudentNotInClassException If the student is not in a class.
     */
    private  Map<String, Object> parseHomeworkHTML(final String raw)
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
                if (gradeString.length > 1) {
                    if (StringUtils.isNumeric(gradeString[1])) {
                        gradeArray.put("received", gradeString[0]);
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
                    assignmentArray.put("course_url", getCourseworkURL(
                            getCcode()));
                } catch (UnexpectedResponseException e) {
                    throw new IllegalStateException("Unable to get URL");
                }

                sectionArray.add(assignmentArray);
            }
            mainArray.put(c.text(), sectionArray);
        }
        return mainArray;
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
        int pDay = Integer.parseInt(rawParts[2]);
        int pHour = Integer.parseInt(rawParts[timeindex].split(":")[0]);
        int pMinute = Integer.parseInt(rawParts[timeindex].split(":")[1]);
        if (rawParts[timeindicatorindex].equals("AM")
                && pHour == timedifference) {
            pHour = 0;
        }
        if (rawParts[timeindicatorindex].equals("PM")) {
            pHour += timedifference;
        }
        return String.format("%s-%02d-%02dT%02d:%02d:00Z",
                CURRENT_YEAR, pMonth, pDay, pHour, pMinute);
    }
}
