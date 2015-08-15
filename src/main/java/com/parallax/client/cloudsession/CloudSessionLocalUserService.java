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
import com.parallax.client.cloudsession.exceptions.EmailAlreadyConfirmedException;
import com.parallax.client.cloudsession.exceptions.InsufficientBucketTokensException;
import com.parallax.client.cloudsession.exceptions.PasswordVerifyException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UnknownUserIdException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michel
 */
public class CloudSessionLocalUserService {

    private final String BASE_URL;
    private final String SERVER;

    public CloudSessionLocalUserService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    public boolean doPasswordReset(String token, String email, String password, String passwordConfirm) throws UnknownUserException, PasswordVerifyException {
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("password", password);
        data.put("password-confirm", passwordConfirm);
        HttpRequest request = HttpRequest.post(getUrl("local/reset/" + email)).form(data);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return true;
        } else {
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserException(responseObject.get("data").getAsString());
                case 460:
                    throw new PasswordVerifyException();
            }
            return false;
        }
    }

    public boolean requestPasswordReset(String email) throws UnknownUserException, InsufficientBucketTokensException {
        HttpRequest request = HttpRequest.get(getUrl("local/reset/" + email)).header("server", SERVER);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return true;
        } else {
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserException(responseObject.get("data").getAsString());
                case 470:
                    throw new InsufficientBucketTokensException(responseObject.get("message").getAsString(), responseObject.get("data").getAsString());
            }
            return false;
        }
    }

    public boolean doConfirm(String email, String token) throws UnknownUserException {
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("token", token);
        HttpRequest request = HttpRequest.post(getUrl("local/confirm")).form(data);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return true;
        } else {
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserException(responseObject.get("data").getAsString());
                case 510:
                    return false;
            }
            return false;
        }
    }

    public boolean requestNewConfirmEmail(String email) throws UnknownUserException, InsufficientBucketTokensException, EmailAlreadyConfirmedException {
        HttpRequest request = HttpRequest.get(getUrl("local/confirm/" + email)).header("server", SERVER);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return true;
        } else {
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserException(responseObject.get("data").getAsString());
                case 470:
                    throw new InsufficientBucketTokensException(responseObject.get("message").getAsString(), responseObject.get("data").getAsString());
                case 520:
                    throw new EmailAlreadyConfirmedException(responseObject.get("message").getAsString());
            }
            return false;
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    public boolean changePassword(Long idUser, String oldPassword, String password, String confirmPassword) throws UnknownUserIdException, PasswordVerifyException {
        Map<String, String> data = new HashMap<>();
        data.put("old-password", oldPassword);
        data.put("password", password);
        data.put("password-confirm", confirmPassword);
        HttpRequest request = HttpRequest.post(getUrl("local/password/" + idUser)).form(data);
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
        String response = request.body();
//        System.out.println(response);
        JsonElement jelement = new JsonParser().parse(response);
        JsonObject responseObject = jelement.getAsJsonObject();
        if (responseObject.get("success").getAsBoolean()) {
            return true;
        } else {
            switch (responseObject.get("code").getAsInt()) {
                case 400:
                    throw new UnknownUserIdException(responseObject.get("data").getAsString());
                case 460:
                    throw new PasswordVerifyException();
                case 510:
                    return false;
            }
            return false;
        }
    }

}
