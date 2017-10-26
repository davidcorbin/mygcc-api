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
 * Class for getting insurance information.
 */
public class Insurance {

    /**
     * The URL to request health insurance information.
     */
    private static final String MYINS =
            "https://my.gcc.edu/html5/apps/fin/models/JSON.ashx"
                    + "?entity=healthinsurance&qry=get&id_num=";

    /**
     * Authorization needed in order to get Insurance data.
     */
    private Authorization auth;

    /**
     * Insurance constructor.
     * @param authorization the authorization object for the user.
     */
    public Insurance(final Authorization authorization) {
        this.auth = authorization;
    }

    /**
     * Gets the insurance data formatted nicely.
     * @return The insurance data in a Map.
     * @throws UnexpectedResponseException If no insurance data is returned.
     */
    public final Map<String, Object> getInsuranceData()
            throws UnexpectedResponseException {
        String rawIns = getContentFromUrl();
        if (rawIns == null) {
            throw new UnexpectedResponseException();
        }
        Map<String, Object> response = parseInsuranceJSON(rawIns);
        return response;
    }

    /**
     * Get insurance info for given id.
     * @return JSON string from myGCC
     * @throws UnexpectedResponseException Unexpected response from myGCC.
     */
    private String getContentFromUrl() throws UnexpectedResponseException {
        final int nullLength = 4;
        try {
            URL gccIns = new URL(MYINS + auth.getUsername());
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
                    http.getInputStream()));
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
    private Map<String, Object> parseInsuranceJSON(final String rawJSON) {
        JSONObject obj = new JSONObject(rawJSON);
        Map<String, Object> prettyInsurance = new HashMap<>();
        if (!obj.getBoolean("HasInsurance")) {
            prettyInsurance.put("usesCollegeInsurance", true);
            return prettyInsurance;
        }
        prettyInsurance.put("usesCollegeInsurance", false);
        Map<String, Object> primaryIns = new HashMap<>();
        Map<String, Object> primarySubscriber = new HashMap<>();
        Map<String, Object> primaryEmployer = new HashMap<>();
        Map<String, Object> secondaryIns = new HashMap<>();
        Map<String, Object> secondarySubscriber = new HashMap<>();
        Map<String, Object> secondaryEmployer = new HashMap<>();
        Map<String, Object> insurance = new HashMap<>();
        Map<String, Object> physician = new HashMap<>();

        primaryIns.put("companyName", obj.getString("InsCompany"));
        primaryIns.put("phone", obj.getString("InsPhone").replaceAll("-", ""));
        primaryIns.put("address", obj.getString("Address"));
        primaryIns.put("policy", obj.getString("PolicyNum"));
        primaryIns.put("group", obj.getString("GroupNum"));
        primarySubscriber.put("name", obj.getString("Subscriber"));
        primaryEmployer.put("name", obj.getString("Employer"));
        primaryEmployer.put("address", obj.getString("EmployerAddress"));
        primarySubscriber.put("employer", primaryEmployer);
        primarySubscriber.put("relationship", obj.getString("Relationship"));
        primaryIns.put("subscriber", primarySubscriber);

        secondaryIns.put("companyName", obj.getString("InsCompany2"));
        secondaryIns.put("phone", obj.getString("InsPhone2")
                .replaceAll("-", ""));
        secondaryIns.put("address", obj.getString("Address2"));
        secondaryIns.put("policy", obj.getString("PolicyNum2"));
        secondaryIns.put("group", obj.getString("GroupNum2"));
        secondarySubscriber.put("name", obj.getString("Subscriber2"));
        secondaryEmployer.put("name", obj.getString("Employer2"));
        secondaryEmployer.put("address", obj.getString("EmployerAddress2"));
        secondarySubscriber.put("employer", secondaryEmployer);
        secondarySubscriber.put("relationship", obj.getString("Relationship2"));
        secondaryIns.put("subscriber", secondarySubscriber);

        physician.put("name", obj.getString("Physician"));
        physician.put("phone", obj.getString("PhysPhone").replaceAll("-", ""));

        insurance.put("primary", primaryIns);
        insurance.put("secondary", secondaryIns);
        prettyInsurance.put("insurance", insurance);
        prettyInsurance.put("physician", physician);

        return prettyInsurance;
    }
}
