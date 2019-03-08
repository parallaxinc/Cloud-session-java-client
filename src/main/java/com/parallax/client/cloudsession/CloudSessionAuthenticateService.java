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
import com.parallax.client.cloudsession.exceptions.InsufficientBucketTokensException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserException;
import com.parallax.client.cloudsession.exceptions.UserBlockedException;
import com.parallax.client.cloudsession.exceptions.WrongAuthenticationSourceException;
import com.parallax.client.cloudsession.objects.User;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticate user login
 * <p>
 * Provides an interface to the Could-Session server for user authentication
 * services. The methods in this class use custom exceptions to indicate 
 * a situation where a program fault has been detected.
 * 
 * @author Michel
 * 
 */
public class CloudSessionAuthenticateService {

    // Handle for any logging activity
    private final Logger LOG = LoggerFactory.getLogger(CloudSessionAuthenticateService.class);

    // Base URL use to obtain authentication service.
    private final String BASE_URL;
    
    //Host name
    private final String SERVER;

    /**
     * Class constructor
     * 
     * @param server The cloud session host name
     * @param baseUrl The cloud session URL as defined in the 
     * blocklyprop.properties file
     */
    public CloudSessionAuthenticateService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;

    }

    /**
     * Authenticate user from local authentication database
     * 
     * @param login is the user email address
     * @param password is the password entered for the login attempt
     *
     * @return a User object if successful, otherwise return null
     *
     * @throws UnknownUserException is thrown when the user email address is not found
     *
     * @throws UserBlockedException is an exception thrown when the requested user
     *                              account is blockled
     *
     * @throws EmailNotConfirmedException is thrown when the requested account is pending
     *                                    email verification
     *
     * @throws InsufficientBucketTokensException
     *      is thrown when the requested activity has occurred too many times within
     *      a specified period of time.
     *
     * @throws WrongAuthenticationSourceException
     *      is thrown when the login attempt is using local authentication when the account
     *      has been set up to use OAuth or when the login attempt is using OAuth when the
     *      account was set up for local authentication
     *
     * @throws ServerException
     *      is thrown when something really bad happens. Think environment or failed access
     *      to the Cloud Session server
     *
     */
    public User authenticateLocalUser(String login, String password) 
            throws
                UnknownUserException, 
                UserBlockedException, 
                EmailNotConfirmedException, 
                InsufficientBucketTokensException, 
                WrongAuthenticationSourceException, 
                ServerException {

        JsonObject responseObject;

        try {
            Map<String, String> data = new HashMap<>();
            data.put("email", login);
            data.put("password", password);
    
            LOG.info("Contacting endpoint {}", BASE_URL + "/authenticate/local");

            // Issue POST request to attempt login
            HttpRequest request = HttpRequest
                    .post(BASE_URL + "/authenticate/local")
                    .header("server", SERVER)
                    .form(data);

            LOG.debug("Return from endpoint call");

            String response = request.body();
            JsonElement jelement = new JsonParser().parse(response);
            responseObject = jelement.getAsJsonObject();
            
            LOG.info("Server response code is: {}", request.code());
            
            if (request.ok()) {
                // The service returned an HTTP 200 code
                LOG.debug("Request returned OK");

                if (responseObject.get("success").getAsBoolean()) {
                    
                    LOG.debug("Request was successful. Decoding user object");
                    // Create and return a user object
                    JsonObject userJson = responseObject.get("user").getAsJsonObject();

                    // Create a new cloud session user object
                    User user = new User();

                    user.setId(userJson.get("id").getAsLong());
                    user.setEmail(userJson.get("email").getAsString());
                    user.setLocale(userJson.get("locale").getAsString());
                    user.setScreenname(userJson.get("screenname").getAsString());
                    user.setAuthenticationSource(userJson.get("authentication-source").getAsString());
                
                    // These dates might be zero for grandfathered accounts
                    user.setBirthMonth(userJson.get("bdmonth").getAsInt());
                    user.setBirthYear(userJson.get("bdyear").getAsInt());
                
                    // This gets stored as a Null if the sponsor address is not supplied
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
            
            if (request.code() == 401) {
                
                LOG.info("Server returned a status 401. Detail code is: {}",responseObject.get("code"));
                
                // Authentication failed. Obtain result code
                String message = responseObject.get("message").getAsString();
                
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserException(login, message);
                    case 410:
                        // Wrong password, but we should report it as an
                        // unknow username OR password to increase ambiguity
                        LOG.info("Wrong password");
                        throw new UnknownUserException(login, message);
                    case 420:
                        throw new UserBlockedException(message);
                    case 430:
                        throw new EmailNotConfirmedException(message);
                    case 470:
                        throw new InsufficientBucketTokensException();
                    case 480:
                        String authenticationSource = responseObject.get("data").getAsString();
                        throw new WrongAuthenticationSourceException(authenticationSource);
                }
                    
                LOG.error("Server returned error code {}", request.code());
                // Encountered a server error
                throw new ServerException(request.message());
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntax error", jse);
            throw new ServerException(jse);
        }
        
        return null;
    }   
}
