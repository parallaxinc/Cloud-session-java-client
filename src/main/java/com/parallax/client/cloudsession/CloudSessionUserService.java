/*
 * Copyright (c) 2019 Parallax Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the “Software”), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

    // Create an instance of the application logger
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionUserService.class);
    
    
    // A string that provides the 'protocol://host.example.com'
    private final String BASE_URL;

    
    
    /**
     * Init the static base URL
     *
     * @param baseUrl defines the URL to use in this setting
     */
    public CloudSessionUserService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }


    /**
     * Retrieve a user profile record with a matching email address
     * 
     * @param email
     * is the unique email address of the user profile to retrieve
     *
     * @return a populated Cloud Session User object if successful
     *
     * @throws UnknownUserException if the provided email address was not found
     *
     * @throws ServerException
     *      if an unexpected error occurs while obtaining the requested user profile.
     */
    public User getUser(String email) throws UnknownUserException, ServerException {
        
        LOG.info("Contacting CloudSession endpoint GET '/user/email/");
        
        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/email/" + email));
            
            if (request.ok()) {
                // Process the JSON response message
                String response = request.body();
                JsonElement jsonElement = new JsonParser().parse(response);
                JsonObject responseObject = jsonElement.getAsJsonObject();

                // Verify that the we have a 'success' message
                if (responseObject.get("success").getAsBoolean()) {
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();
                    return populateUser(userJson);
                } else {
                    // Parse the embedded error message
                    String message = responseObject.get("message").getAsString();

                    if (responseObject.get("code").getAsInt() == 400) {
                        throw new UnknownUserException(email, message);
                    }
                    throw new ServerException("Unknown response code.");
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
     * @param screenName is the user profile screen name
     *
     * @return  a fully populated Cloud Session User object if successful
     *
     * @throws UnknownUserException
     *      when the user scree name cannot be found
     *
     * @throws ServerException
     *      when an unexpected error occurs
     */
    @Deprecated
    public User getUserByScreenname(String screenName) throws UnknownUserException, ServerException {
        
        LOG.debug("Contacting endpoint '/user/screenname/{name}", screenName);

        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/screenname/" + screenName));
            
            if (request.ok()) {
                String response = request.body();
                JsonElement jsonElement = new JsonParser().parse(response);
                JsonObject responseObject = jsonElement.getAsJsonObject();
                
                if (responseObject.get("success").getAsBoolean()) {
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();
                    return populateUser(userJson);
                } else {
                    String message = responseObject.get("message").getAsString();
                    if (responseObject.get("code").getAsInt() == 400) {
                        throw new UnknownUserException(screenName, message);
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
     * @param idUser Primary key id of the user profile
     *
     * @return a fully populated Cloud Session User object if successful
     *
     * @throws UnknownUserIdException
     *      when a user profile matching the submitted user id cannot be found
     *
     * @throws ServerException
     *      when an unexpected error occurs
     */
    public User getUser(Long idUser) throws 
            UnknownUserIdException,
            ServerException,
            EmailNotConfirmedException {

        LOG.info("Contacting endpoint '/user/id/{}", idUser);
        
        try {
            HttpRequest request = HttpRequest.get(getUrl("/user/id/" + idUser));
            
            if (request.ok()) {
                LOG.debug("CloudSession /user/id/{} endpoint reports success", idUser);

                // Get the details returned from the service
                String response = request.body();

                JsonElement jsonElement = new JsonParser().parse(response);
                JsonObject responseObject = jsonElement.getAsJsonObject();
                
                if (responseObject.get("success").getAsBoolean()) {
                    LOG.debug("CloudSession payload reports success.");
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();

                    LOG.debug("User object: {}", userJson.toString());
                    return populateUser(userJson);
                } else {
                    LOG.error("CloudSession payload reports error code: {}", responseObject.get("code"));
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
     * @param idUser Primary key id of the user profile
     *
     * @param screenname is the user profile screen name
     *
     * @return a fully populated Cloud Session User object if successful
     *
     * @throws UnknownUserIdException
     *      when a user profile matching the submitted user id cannot be found
     *
     * @throws ScreennameUsedException
     *      when the submitted screen name has already been used by another user profile
     *
     * @throws ServerException
     *      when an unexpected error occurs
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
     * @param idUser Primary key id of the user profile
     *
     * @param locale is the user's language assignment
     *
     * @return a fully populated Cloud Session User object if successful
     *
     * @throws UnknownUserIdException
     *      when a user profile matching the submitted user id cannot be found
     *
     * @throws ServerException
     *      when an unexpected error occurs
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
                    if (responseObject.get("code").getAsInt() == 400) {
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
     * @param userJson is a Json object that contains user profile details
     *
     * @return a fully populated CloudSession User object
     */
    private User populateUser(JsonObject userJson) {

        LOG.debug("Hydrating CloudSession User object from Json data");

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
        LOG.debug("Returning a hydrated CloudSession User object");

        return user;
    }
}
