/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michel
 */
public class CloudSessionServer {

    private final String BASE_URL;

    public CloudSessionServer(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    public boolean authenticateLocalUser(String login, String password) {
        Map<String, String> data = new HashMap<>();
        data.put("email", login);
        data.put("password", password);
        String response = HttpRequest.post(getUrl("authenticate/local")).form(data).body();
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return true;
        } else {
            System.out.println(response);
            return false;
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

}
