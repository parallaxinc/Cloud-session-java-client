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
import com.parallax.client.cloudsession.exceptions.EmailNotConfirmedException;
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
 * <p>
 * Each public method will return a JSON document if the call is successful; 
 * a null if the call is unsuccessful but the cause is not anticipated; or
 * the method will throw an exception of a failure that was anticipated.
 * 
 * @author Michel
 */
public class CloudSessionUserService {

    
    /**
     * 
     */
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionUserService.class);
    
    
    /**
     * A string that provides the 'protocol://host.example.com'
     */
    private final String BASE_URL;

    
    
    /**
     *
     * @param baseUrl
     */
    public CloudSessionUserService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }


    /**
     * Retrieve a user record with a matching email address
     * 
     * @param email
     * @return
     * @throws UnknownUserException
     * @throws ServerException
     */
    public User getUser(String email) throws UnknownUserException, ServerException {
        
        LOG.debug("Contacting endpoint '/user/email/{email}");
        
        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/email/" + email));
            
            if (request.ok()) {
                // Process the JSON response message
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject responseObject = jelement.getAsJsonObject();

                // Verify that the we have a 'success' message
                if (responseObject.get("success").getAsBoolean()) {
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();
                    return populateUser(userJson);
                } else {
                    // Parse the embedded error message
                    String message = responseObject.get("message").getAsString();
                    switch (responseObject.get("code").getAsInt()) {
                        case 400:
                            throw new UnknownUserException(email, message);
                        default:
                            throw new ServerException("Unknown response code.");
                    }
                }
            } else {
                LOG.error("Unable to contact Cloud Session endpoint '/user/email/', Code: {}", request.code());
                return null;
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
        
        LOG.debug("Contacting endpoint '/user/screenname/{name}");

        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/screenname/" + screenname));
            
            if (request.ok()) {
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject responseObject = jelement.getAsJsonObject();
                
                if (responseObject.get("success").getAsBoolean()) {
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();
                    return populateUser(userJson);
                } else {
                    String message = responseObject.get("message").getAsString();
                    if (responseObject.get("code").getAsInt() == 400) {
                        throw new UnknownUserException(screenname, message);
                    } else {
                        return null;
                    }
                }
            }

        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
        
        return null;
    }

    
    /**
     * Retrieve a user record from the user ID key
     * 
     * @param idUser
     * @return
     * @throws UnknownUserIdException
     * @throws ServerException
     */
    public User getUser(Long idUser) throws 
            UnknownUserIdException,
            ServerException,
            EmailNotConfirmedException {

        LOG.info("Contacting endpoint '/user/id/{uid}");
        
        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/id/" + idUser));
            
            if (request.ok()) {
                LOG.info("Endpoint reports success: {}", request.body());
                        ;
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject responseObject = jelement.getAsJsonObject();
                
                if (responseObject.get("success").getAsBoolean()) {
                    LOG.info("Payload reports success.");
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();
                    return populateUser(userJson);
                } else {
                    LOG.error("Payload reports error code: {}", responseObject.get("code"));
                    int resultCode = responseObject.get("code").getAsInt();
                    
                    if (resultCode == 400) {
                        throw new UnknownUserIdException(idUser);
                    }
                    else if (resultCode == 430) {
                        throw new EmailNotConfirmedException("");
                    }
 
                    return null;
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
        
        return null;
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

        LOG.debug("Contacting endpoint '/user/info/{uid}");
        
        try {
            // Set data that is being sent to the server
            Map<String, String> data = new HashMap<>();
            data.put("screenname", screenname);
            
            HttpRequest request = HttpRequest.post(getUrl("/user/info/" + idUser)).form(data);
            
            if (request.ok()) {
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
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
        
        return null;
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

        LOG.debug("Contacting endpoint '/user/locale/{uid}");
        
        try {
            
            Map<String, String> data = new HashMap<>();
            data.put("locale", locale);

            HttpRequest request = HttpRequest.post(getUrl("/user/locale/" + idUser)).form(data);
            
            if (request.ok()) {
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
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax service error", jse);
            throw new ServerException(jse);
        }
        
        return null;
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }


    /**
     * Populate an user object from a JSON document
     * 
     * @param userJson
     * @return 
     */
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
