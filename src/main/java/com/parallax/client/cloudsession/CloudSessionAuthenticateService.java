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
import com.parallax.client.cloudsession.exceptions.EmailNotConfirmedException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UserBlockedException;
import com.parallax.client.cloudsession.objects.User;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michel
 */
public class CloudSessionAuthenticateService {

    private final Logger LOG = LoggerFactory.getLogger(CloudSessionAuthenticateService.class);
    private final String BASE_URL;

    public CloudSessionAuthenticateService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    public User authenticateLocalUser(String login, String password) throws UnknownUserException, UserBlockedException, EmailNotConfirmedException, ServerException {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", login);
            data.put("password", password);
            HttpRequest httpRequest = HttpRequest.post(getUrl("/authenticate/local")).form(data);
            String response = httpRequest.body();

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
                        throw new UnknownUserException(login, message);
                    case 410:
                        // Wrong password
                        return null;
                    case 420:
                        throw new UserBlockedException(message);
                    case 430:
                        throw new EmailNotConfirmedException(message);
                }
                return null;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

}
