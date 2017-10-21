package com.mygcc.api;

import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for getting insurance information.
 */
@Path("/1/user/")
public class InsuranceResource {

    /**
     * Main function for getting insurance.
     * @param token Student id.
     * @return formatted insurance information.
     * @throws Exception When url is not formatted right.
     */
    @GET
    @Path("insurance")
    @Produces(MediaType.APPLICATION_JSON)
    public final Response insuranceResponse(
            @HeaderParam("Authorization") final String token)
            throws Exception {
        Map<String, Object> response = new HashMap<>();
        // Check if token is null.
        if (token == null) {
            response.put("date", Instant.now().getEpochSecond());
            response.put("message",
                    "Authorization header empty or does not exist.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }
        // Else get insurance info.
        String rawIns = getContentFromUrl(token);

        //Check if insurance info exists.
        if (rawIns == null) {
            response.put("date", Instant.now().getEpochSecond());
            response.put("message", "Insurance info not found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }

        Map<String, Object> insuranceInfo = parseInsuranceJSON(rawIns);

        return Response.status(Response.Status.OK)
                .entity(insuranceInfo)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    /**
     * Get insurance info for given id.
     * @param id student id number.
     * @return JSON string from myGCC
     * @throws Exception When url is not formatted right.
     */
    private String getContentFromUrl(final String id) throws Exception {
        final int nullLength = 4;
        String gccUrl = "https://my.gcc.edu/html5/apps/fin/models/JSON.ashx";
        String insParams = "?entity=healthinsurance&qry=get&id_num=";
        URL gccIns = new URL(gccUrl + insParams + id);
        HttpURLConnection yc = (HttpURLConnection) gccIns.openConnection();

        // Check for invalid id.
        // If the info does not exist then 'null' is returned from gcc.
        if (yc.getContentLength() == nullLength) {
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(
                yc.getInputStream()));
        String inputLine;
        StringBuilder output = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            output.append(inputLine);
        }
        in.close();
        return output.toString();
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
