package com.mygcc;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
            response.put("message", "Authorization header empty or does not exist.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(response)
                    .build();
        }
        // Else get insurance info.
        String insuranceInfo = getContentFromUrl(token);

        //Check if insurance info exists.
        if (insuranceInfo.equals("null")) {
            response.put("date", Instant.now().getEpochSecond());
            response.put("message", "Insurance info not found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(response)
                    .build();
        }
        response.put("insurance", insuranceInfo);
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    /**
     * Get insurance info for given id.
     * @param id student id number.
     * @return JSON string from myGCC
     * @throws Exception When url is not formatted right.
     */
    private String getContentFromUrl(final String id) throws Exception {
        String gccUrl = "https://my.gcc.edu/html5/apps/fin/models/JSON.ashx";
        String insParams = "?entity=healthinsurance&qry=get&id_num=";
        URL gccIns = new URL(gccUrl + insParams + id);
        URLConnection yc = gccIns.openConnection();
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
}
