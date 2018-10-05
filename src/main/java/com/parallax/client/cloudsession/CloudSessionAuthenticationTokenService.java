/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parallax.client.cloudsession;

import com.github.kevinsawicki.http.HttpRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import com.parallax.client.cloudsession.exceptions.EmailNotConfirmedException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownUserIdException;
import com.parallax.client.cloudsession.exceptions.UserBlockedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michel
 */
public class CloudSessionAuthenticationTokenService {

    private final Logger LOG = LoggerFactory.getLogger(CloudSessionAuthenticationTokenService.class);
    
    private final String BASE_URL;
    private final String SERVER;

    /**
     *
     * @param server
     * @param baseUrl
     */
    public CloudSessionAuthenticationTokenService(String server, String baseUrl) {
        this.SERVER = server;
        this.BASE_URL = baseUrl;
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    /**
     *
     * @param idUser
     * @param browser
     * @param ipAddress
     * @return
     * @throws UnknownUserIdException
     * @throws UserBlockedException
     * @throws EmailNotConfirmedException
     * @throws ServerException
     */
    public String request(Long idUser, String browser, String ipAddress) throws
            UnknownUserIdException,
            UserBlockedException,
            EmailNotConfirmedException,
            ServerException {
        
        LOG.debug("Contacting endpoint '/authtoken/request");

        try {
            Map<String, String> data = new HashMap<>();
            data.put("idUser", String.valueOf(idUser));
            data.put("browser", browser);
            data.put("ipAddress", ipAddress);
            
            HttpRequest request = HttpRequest
                    .post(getUrl("/authtoken/request"))
                    .header("server", SERVER)
                    .form(data);
            
            if (request.ok()) {
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject responseObject = jelement.getAsJsonObject();
                
                if (responseObject.get("success").getAsBoolean()) {
                    return responseObject.get("token").getAsString();
                } else {
                    switch (responseObject.get("code").getAsInt()) {
                        case 400:
                            throw new UnknownUserIdException(responseObject.get("data").getAsString());
                        case 420:
                            throw new UserBlockedException();
                        case 430:
                            throw new EmailNotConfirmedException();
                    }
                    
                return null;
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
        
        return null;
    }
    

    /**
     *
     * @param token
     * @param idUser
     * @param browser
     * @param ipAddress
     * @return
     * @throws ServerException
     */
    public boolean doConfirm(String token, Long idUser, String browser, String ipAddress) throws ServerException {

        LOG.debug("Contacting endpoint '/authtoken/confirm");
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            data.put("idUser", String.valueOf(idUser));
            data.put("browser", browser);
            data.put("ipAddress", ipAddress);

            HttpRequest request = HttpRequest
                    .post(getUrl("/authtoken/confirm"))
                    .header("server", SERVER)
                    .form(data);

            if (request.ok()) {
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonObject responseObject = jelement.getAsJsonObject();
                
                if (responseObject.get("success").getAsBoolean()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
        
        return false;
    }

    
    /**
     *
     * @param idUser
     * @param browser
     * @param ipAddress
     * @return
     * @throws ServerException
     */
    public List<String> getTokens(Long idUser, String browser, String ipAddress) throws ServerException {

        LOG.debug("Contacting endpoint '/authtoken/tokens");
        
        try {
            Map<String, String> data = new HashMap<>();
            data.put("browser", browser);
            data.put("ipAddress", ipAddress);
            
            HttpRequest request = HttpRequest
                    .post(getUrl("/authtoken/tokens/" + idUser))
                    .header("server", SERVER)
                    .form(data);
            
            if (request.ok()) {
                String response = request.body();
                JsonElement jelement = new JsonParser().parse(response);
                JsonArray jsonTokens = jelement.getAsJsonArray();
                List<String> tokens = new ArrayList<>();
                
                for (JsonElement token : jsonTokens) {
                    tokens.add(token.getAsString());
                }
                
                return tokens;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        } catch (JsonSyntaxException jse) {
            LOG.error("Json syntace service error", jse);
            throw new ServerException(jse);
        }
        
        return null;
    }

}
