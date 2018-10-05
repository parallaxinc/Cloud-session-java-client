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
import com.parallax.client.cloudsession.exceptions.ScreennameUsedException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UnknownUserIdException;
import com.parallax.client.cloudsession.objects.User;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide user account services for existing local user accounts
 * 
 * @author Michel
 */
public class CloudSessionUserService {

    /**
     * Instance of the application logging facility
     */
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionUserService.class);

    /**
     * The root component of the Cloud Session REST service URL
     */
    private final String BASE_URL;

    /**
     *
     * @param baseUrl
     * The root component of the Cloud Session REST service URL
     */
    public CloudSessionUserService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    /**
     * Retrieve a user record with a matching email address
     * 
     * @param email
     * The email address associated with the user account
     *
     * @return
     * Returns a valid, populated User object
     *
     * @throws UnknownUserException
     *
     * @throws ServerException
     */
    public User getUser(String email)
            throws UnknownUserException, ServerException {

        try {
            LOG.info("Sending request to CS: {}", getUrl("/user/email/"));

            // Place a request to the cloud session service. Trap any response
            // that indicates an error
            HttpRequest request;

            try {
                request = HttpRequest.get(getUrl("/user/email/" + email));

                LOG.info("Request response code: {}", request.code());

                if (!request.ok()) {
                    LOG.error("Error contacting cloud session service: {}", request.code());
                    throw new ServerException(String.format("Response code {} returned.", request.code()));
                }
            } catch (HttpRequest.HttpRequestException hex) {
                LOG.warn("REST service request failed. The reported error is: {}", hex.getMessage());
                throw new ServerException(hex);
            }

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            // Verify that we have a valid response object
            if (responseObject.get("success").getAsBoolean()) {
                JsonObject userJson = responseObject.get("user").getAsJsonObject();
                return populateUser(userJson);
            } else {
                String message = responseObject.get("message").getAsString();
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(email, message);
                    default:
                        throw new ServerException("Unknown response code.");
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        } catch (java.lang.NullPointerException npe) {
            LOG.error("Null pointer detected. Maybe we didn't get a valid Response object. Msg: ", npe);
            return null;
        }
    }

    /**
     * Retrieve a user record with a matching screen name
     * 
     * @param screenname
     * @return
     * @throws UnknownUserException
     * @throws ServerException
     */
    public User getUserByScreenname(String screenname) throws UnknownUserException, ServerException {
        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/screenname/" + screenname));
//        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
            String response = request.body();
//        System.out.println(response);
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                JsonObject userJson = responseObject.get("user").getAsJsonObject();
                return populateUser(userJson);
            } else {
                String message = responseObject.get("message").getAsString();
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(screenname, message);
                }
                return null;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     * Retrieve a user record from the user ID key
     * 
     * @param idUser
     * @return
     * @throws UnknownUserIdException
     * @throws ServerException
     */
    public User getUser(Long idUser) throws UnknownUserIdException, ServerException {
        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/id/" + idUser));
            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                JsonObject userJson = responseObject.get("user").getAsJsonObject();
                return populateUser(userJson);
            } else {
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserIdException(idUser);
                }
                return null;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     * Set user screen name on user record keyed on the user ID
     * 
     * @param idUser
     * @param screenname
     * @return
     * @throws UnknownUserIdException
     * @throws ScreennameUsedException
     * @throws ServerException
     */
    public User changeUserInfo(Long idUser, String screenname)
            throws UnknownUserIdException, ScreennameUsedException, ServerException {
        
        try {
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
                return populateUser(userJson);
            } else {
                String message = responseObject.get("message").getAsString();
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserIdException(idUser, message);
                    case 500:
                        throw new ScreennameUsedException(responseObject.get("data").getAsString());
                }
                return null;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
    }

    /**
     * Update the locale on the user record keyed on the user ID
     * 
     * @param idUser
     * @param locale
     * @return
     * @throws UnknownUserIdException
     * @throws ServerException
     */
    public User changeUserLocale(Long idUser, String locale) throws UnknownUserIdException, ServerException {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("locale", locale);

            HttpRequest request = HttpRequest.post(getUrl("/user/locale/" + idUser)).form(data);
            String response = request.body();

            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();

            if (responseObject.get("success").getAsBoolean()) {
                JsonObject userJson = responseObject.get("user").getAsJsonObject();
                return populateUser(userJson);
            } else {
                String message = responseObject.get("message").getAsString();
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserIdException(idUser, message);
                }
                return null;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    private User populateUser(JsonObject userJson) {
        
        User user = new User();
        
        user.setId(userJson.get("id").getAsLong());
        user.setEmail(userJson.get("email").getAsString());
        user.setLocale(userJson.get("locale").getAsString());
        user.setScreenname(userJson.get("screenname").getAsString());
        user.setAuthenticationSource(userJson.get("authentication-source").getAsString());
        
        // COPPA update
        user.setBirthMonth(userJson.get("bdmonth").getAsInt());
        user.setBirthYear(userJson.get("bdyear").getAsInt());
                
        if (userJson.get("parent-email").isJsonNull()) {
            user.setCoachEmail("");
        }
        else {
            user.setCoachEmail(userJson.get("parent-email").getAsString());
        }
                
        user.setCoachEmailSource(userJson.get("parent-email-source").getAsInt());
                
        return user;
    }
    
}
