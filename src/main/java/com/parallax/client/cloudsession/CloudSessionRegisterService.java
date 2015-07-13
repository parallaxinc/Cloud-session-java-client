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
import com.parallax.client.cloudsession.exceptions.NonUniqueEmailException;
import com.parallax.client.cloudsession.exceptions.PasswordVerifyException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michel
 */
public class CloudSessionRegisterService {

    private final String BASE_URL;
    private final String SERVER;

    public CloudSessionRegisterService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    public Long registerUser(String email, String password, String passwordConfirm, String language) throws NonUniqueEmailException, PasswordVerifyException {
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("password", password);
        data.put("password-confirm", passwordConfirm);
        data.put("language", language);
        HttpRequest request = HttpRequest.put(getUrl("user/register")).header("server", SERVER).form(data);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return responseObject.get("user").getAsLong();
        } else {
            switch (responseObject.get("code").getAsInt()) {
                case 450:
                    throw new NonUniqueEmailException(responseObject.get("data").getAsString());
                case 460:
                    throw new PasswordVerifyException();
            }
            return null;
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

}
