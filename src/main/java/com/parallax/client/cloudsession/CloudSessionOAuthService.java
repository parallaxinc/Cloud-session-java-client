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
import com.google.gson.JsonSyntaxException;
import com.parallax.client.cloudsession.exceptions.NonUniqueEmailException;
import com.parallax.client.cloudsession.exceptions.ScreennameUsedException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.WrongAuthenticationSourceException;
import com.parallax.client.cloudsession.objects.User;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michel
 */
public class CloudSessionOAuthService {

    private final Logger LOG = LoggerFactory.getLogger(CloudSessionOAuthService.class);
    private final String BASE_URL;
    private final String SERVER;

    /**
     *
     * @param server
     * @param baseUrl
     */
    public CloudSessionOAuthService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    
    /**
     *
     * @param login
     * @param authenticationSource
     * @return
     * @throws UnknownUserException
     * @throws WrongAuthenticationSourceException
     * @throws ServerException
     */
    public User validateUser(String login, String authenticationSource) throws UnknownUserException, WrongAuthenticationSourceException, ServerException {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", login);
            data.put("source", authenticationSource);
            
            HttpRequest request = HttpRequest
                    .post(getUrl("/oauth/validate"))
                    .header("server", SERVER).form(data);
            
            if (request.ok()) {
                String response = request.body();
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
                        case 480:
                            String userAuthenticationSource = responseObject.get("data").getAsString();
                            throw new WrongAuthenticationSourceException(userAuthenticationSource);
                    }

                    LOG.warn("Unexpected error: {}", response);
                    return null;
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax error: {}", jse.getMessage());
            throw new ServerException(jse);
        }
        
        return null;
    }

    /**
     *
     * @param email
     * @param authenticationSource
     * @param locale
     * @param screenname
     * @return
     * @throws NonUniqueEmailException
     * @throws ScreennameUsedException
     * @throws ServerException
     */
    public Long registerUser(
            String email, 
            String authenticationSource, 
            String locale, 
            String screenname) throws
                    NonUniqueEmailException, 
                    ScreennameUsedException, 
                    ServerException {

        try {
            // Prepare payload to send with REST request
            Map<String, String> data = new HashMap<>();
            data.put("email", email);
            data.put("source", authenticationSource);
            data.put("locale", locale);
            data.put("screenname", screenname);
            
            HttpRequest request = HttpRequest
                    .post(getUrl("/oauth/create"))
                    .header("server", SERVER)
                    .form(data);

            if (request.ok()) {
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject responseObject = jelement.getAsJsonObject();
            
                if (responseObject.get("success").getAsBoolean()) {
                    return responseObject.get("user").getAsLong();
                } else {
                    switch (responseObject.get("code").getAsInt()) {
                        case 450:
                            throw new NonUniqueEmailException(responseObject.get("data").getAsString());
                        case 500:
                            throw new ScreennameUsedException(responseObject.get("data").getAsString());
                    }
                    
                    return null;
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
            
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax error: {}", jse.getMessage());
            throw new ServerException(jse);
        }
        
        return null;
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

}
