package com.mygcc.datacollection;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for getting contact information.
 */
public class Contact {

    /**
     * The URL to request health contact information.
     */
    private static final String MYCON =
            "https://my.gcc.edu/html5/apps/stulife/models/JSON.ashx"
                    + "?entity=contact&qry=get&id_num=";

    /**
     * Authorization needed in order to get contact data.
     */
    private Session auth;

    /**
     * contact constructor.
     * @param token the authorization object for the user.
     */
    public Contact(final Token token) {
        this.auth = new Session(token);
    }

    /**
     * Gets the contact data formatted nicely.
     * @return The contact data in a Map collection, where the key is the
     *  name of the contact field (e.g. Address, etc.) and the value is
     *  entered data of that field.
     * @throws UnexpectedResponseException If no contact data is returned.
     */
    public final Map<String, Object> getContactData()
            throws UnexpectedResponseException {
        String rawCon = getContentFromUrl();
        if (rawCon == null) {
            throw new UnexpectedResponseException();
        }
        Map<String, Object> response = parseContactJSON(rawCon);
        return response;
    }

    /**
     * Get contact info for given id.
     * @return JSON string from myGCC
     * @throws UnexpectedResponseException Unexpected response from myGCC.
     */
    private String getContentFromUrl() throws UnexpectedResponseException {
        final int nullLength = 4;
        try {
            URL gccIns = new URL(MYCON + auth.getToken().getUsername());
            HttpURLConnection http = (HttpURLConnection) gccIns
                    .openConnection();
            http.setRequestProperty("Cookie",
                    "ASP.NET_SessionId=" + auth.getSessionID());

            // Check for invalid id.
            // If the info does not exist then 'null' is returned from gcc.
            if (http.getContentLength() == nullLength) {
                return null;
            }
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
     * Parses raw JSON from myGCC and gets the relevant info.
     * @param rawJSON The JSON as given by myGCC
     * @return parsed JSON, with only the wanted info.
     */
    private Map<String, Object> parseContactJSON(final String rawJSON) {
        JSONObject obj = new JSONObject(rawJSON);
        Map<String, Object> prettycontact = new HashMap<>();

        Map<String, Object> student = new HashMap<>();
        Map<String, Object> father = new HashMap<>();
        Map<String, Object> mother = new HashMap<>();
        Map<String, Object> secondary = new HashMap<>();

        Map<String, Object> address = new HashMap<>();
        Map<String, Object> studentPhone = new HashMap<>();
        Map<String, Object> fatherPhone = new HashMap<>();
        Map<String, Object> motherPhone = new HashMap<>();
        Map<String, Object> secondaryAddress = new HashMap<>();

        // Student
        address.put("street", obj.getString("Address1"));
        address.put("street2", obj.getString("Address2"));
        address.put("city", obj.getString("City"));
        address.put("state", obj.getString("State"));
        address.put("zipcode", obj.getString("Zip"));
        address.put("country", obj.getString("Country"));
        student.put("address", address);

        studentPhone.put("home", obj.getString("Phone"));
        studentPhone.put("mobile", obj.getString("Mobile"));
        student.put("phone", studentPhone);

        student.put("email", obj.getString("Email"));

        // Father
        father.put("name", obj.getString("FatherName"));
        father.put("occupation", obj.getString("FatherOccupation"));
        father.put("email", obj.getString("FatherEmail"));

        fatherPhone.put("work", obj.getString("FatherWork"));
        fatherPhone.put("mobile", obj.getString("FatherMobile"));
        father.put("phone", fatherPhone);

        // Mother
        mother.put("name", obj.getString("MotherName"));
        mother.put("occupation", obj.getString("MotherOccupation"));
        mother.put("email", obj.getString("MotherEmail"));

        motherPhone.put("work", obj.getString("MotherWork"));
        motherPhone.put("mobile", obj.getString("MotherMobile"));
        mother.put("phone", motherPhone);

        // Secondary
        secondary.put("name", obj.getString("SeparatedParent"));

        secondaryAddress.put("street", obj.getString("SeparatedAddress1"));
        secondaryAddress.put("street2", obj.getString("SeparatedAddress2"));
        secondaryAddress.put("city", obj.getString("SeparatedCity"));
        secondaryAddress.put("state", obj.getString("SeparatedState"));
        secondaryAddress.put("zipcode", obj.getString("SeparatedZip"));
        secondaryAddress.put("country", obj.getString("SeparatedCountry"));

        secondary.put("address", secondaryAddress);
        secondary.put("phone", obj.getString("SeparatedPhone"));

        prettycontact.put("student", student);
        prettycontact.put("father", father);
        prettycontact.put("mother", mother);
        prettycontact.put("secondary", secondary);

        return prettycontact;
    }
}
