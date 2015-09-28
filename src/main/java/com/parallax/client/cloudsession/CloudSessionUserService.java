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
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UnknownUserIdException;
import com.parallax.client.cloudsession.objects.User;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michel
 */
public class CloudSessionUserService {

    private final String BASE_URL;

    public CloudSessionUserService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    public User getUser(String email) throws UnknownUserException {
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        HttpRequest request = HttpRequest.get(getUrl("/user/email/" + email));
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            JsonObject userJson = responseObject.get("user").getAsJsonObject();
            User user = new User();
            user.setId(userJson.get("id").getAsLong());
            user.setEmail(userJson.get("email").getAsString());
            user.setLocale(userJson.get("locale").getAsString());
            user.setScreenname(userJson.get("screenname").getAsString());
            return user;
        } else {
            String message = responseObject.get("message").getAsString();
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserException(email, message);
            }
            return null;
        }
    }

    public User changeUserInfo(Long idUser, String screenname) throws UnknownUserIdException {
        Map<String, String> data = new HashMap<>();
        data.put("screenname", screenname);
        HttpRequest request = HttpRequest.post(getUrl("/user/info/" + idUser)).form(data);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            JsonObject userJson = responseObject.get("user").getAsJsonObject();
            User user = new User();
            user.setId(userJson.get("id").getAsLong());
            user.setEmail(userJson.get("email").getAsString());
            user.setLocale(userJson.get("locale").getAsString());
            user.setScreenname(userJson.get("screenname").getAsString());
            return user;
        } else {
            String message = responseObject.get("message").getAsString();
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserIdException(idUser, message);
            }
            return null;
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

}
