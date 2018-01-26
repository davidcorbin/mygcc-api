package com.mygcc.datacollection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class for getting homework information.
 */
public class Collaboration extends ClassData {
    /**
     * Default constructor.
     * @param token The auth token.
     * @param courseCode The course code of the specific class.
     */
    public Collaboration(final Token token, final String courseCode) {
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
        String hwURL = getCollaborationURL(getCcode());
        String rawHTML = getContentFromUrl(hwURL);

        // Workaround for abstract class.
        Map<String, Object> listWrapper = new HashMap<>();
        listWrapper.put("data", parseCollaborationHTML(rawHTML));
        return listWrapper;
    }

    /**
     * Turns ugly HTML collaboration page into pretty JSON collaboration list.
     * @param raw The raw unformatted HTML from myGCC.
     * @return A JSON formatted collaboration.
     * @throws ClassDoesNotExistException If the class does not exist.
     * @throws StudentNotInClassException If the student is not in a class.
     */
    private  List<Object> parseCollaborationHTML(final String raw)
            throws ClassDoesNotExistException, StudentNotInClassException {
        List<Object> mainArray = new LinkedList<>();
        Document doc = Jsoup.parse(raw);
        Elements notFound = doc.select(".notFound");
        if (notFound.text().contains("require you to be")) {
            throw new ClassDoesNotExistException();
        } else if (notFound.text().contains("permissions to view")) {
            throw new StudentNotInClassException();
        }

        Elements rows = doc.select(".pContent tr");
        for (Element currRow : rows) {
            Elements students = currRow.select("td");
            for (Element currStudent: students) {
                Map<String, Object> studentMap = new HashMap<>();
                String studentName = currStudent.select(".accessibility")
                        .text();
                String[] nameParts = studentName.split(", ");
                if (nameParts.length > 1) {
                    studentName = nameParts[1] + " " + nameParts[0];
                }

                String imageURL = currStudent.select(".gPhotoImage")
                        .attr("src");

                String studentId = "";
                if (!imageURL.isEmpty()) {
                    studentId = imageURL.substring(imageURL
                                    .lastIndexOf("/") + 1,
                            imageURL.indexOf(".jpg"));
                } else {
                    continue;
                }

                String role = currStudent.select(".HLRoleItem").text();
                Boolean isFaculty = role.contains("Faculty");

                studentMap.put("name", studentName);
                studentMap.put("id", studentId);
                studentMap.put("image", imageURL);
                studentMap.put("isFaculty", isFaculty);
                mainArray.add(studentMap);
            }
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
}
