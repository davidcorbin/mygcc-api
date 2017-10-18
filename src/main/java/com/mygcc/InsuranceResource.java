package com.mygcc;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


@Path("/1/user/")
public class InsuranceResource {
    @GET
    @Path("insurance")
    @Produces(MediaType.APPLICATION_JSON)
    public Response insuranceResponse(@HeaderParam("Authorization") String token) {
        HttpURLConnection.setFollowRedirects(true);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return Response.status();
    }
}
