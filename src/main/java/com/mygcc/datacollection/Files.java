package com.mygcc.datacollection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class for getting files.
 */
public class Files extends ClassData {
    /**
     * Default constructor.
     * @param token The auth token.
     * @param courseCode The course code of the specific class.
     */
    public Files(final Token token, final String courseCode) {
        setAuth(new Session(token));
        setCcode(sanitizeCourseCode(courseCode));
    }

    /**
     * Get the files of a user's class.
     * @return A list of files for that user's class.
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
        String hwURL = courseCodeToURL(getCcode());
        String rawHTML = getContentFromUrl(hwURL);

        // Workaround for abstract class.
        Map<String, Object> listWrapper = new HashMap<>();
        listWrapper.put("data", parseFilesHTML(rawHTML));
        return listWrapper;
    }

    /**
     * Turns ugly HTML files page into pretty JSON files list.
     * @param raw The raw unformatted HTML from myGCC.
     * @return JSON formatted files.
     * @throws ClassDoesNotExistException If the class does not exist.
     * @throws StudentNotInClassException If the student is not in a class.
     */
    private List<Object> parseFilesHTML(final String raw)
            throws ClassDoesNotExistException, StudentNotInClassException {
        final int gradestringlength = 4;
        final int percentindex = 3;
        List<Object> mainArray = new LinkedList<>();
        Document doc = Jsoup.parse(raw);
        Elements notFound = doc.select(".notFound");
        if (notFound.text().contains("require you to be")) {
            throw new ClassDoesNotExistException();
        } else if (notFound.text().contains("permissions to view")) {
            throw new StudentNotInClassException();
        }
        Elements handoutsSection = doc.select(
                ".Handouts tbody.gbody tr");
        for (Element c : handoutsSection) {
            Map<String, Object> fileMap = new HashMap<>();
            Elements currFile = c.select("a");
            String fileUrl = currFile.attr("href");
            String fileName = currFile.text();
            String[] fileInfo = c.select("td").first().ownText()
                    .replaceAll("[\\(\\)]", "").split(", ");
            String fileType = fileInfo[0];
            String fileSize = fileInfo[1];
            fileMap.put("url", fileUrl);
            fileMap.put("name", fileName);
            fileMap.put("type", fileType);
            fileMap.put("size", fileSize);
            mainArray.add(fileMap);
        }
        return mainArray;
    }
}
