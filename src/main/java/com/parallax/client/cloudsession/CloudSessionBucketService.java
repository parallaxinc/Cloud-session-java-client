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
import com.parallax.client.cloudsession.exceptions.InsufficientBucketTokensException;
import com.parallax.client.cloudsession.exceptions.ServerException;
import com.parallax.client.cloudsession.exceptions.UnknownBucketTypeException;
import com.parallax.client.cloudsession.exceptions.UnknownUserIdException;
import com.parallax.client.cloudsession.exceptions.UserBlockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Michel
 */
public class CloudSessionBucketService {

    private final Logger LOG = LoggerFactory.getLogger(CloudSessionBucketService.class);
    private final String BASE_URL;

    public CloudSessionBucketService(String baseUrl) {
        this.BASE_URL = baseUrl;
    }

    public boolean consumeOne(String type, Long id) throws UnknownUserIdException, UnknownBucketTypeException, InsufficientBucketTokensException, EmailNotConfirmedException, UserBlockedException, ServerException {
        HttpRequest request = HttpRequest.get(getUrl("/bucket/consume/" + type + "/" + id));
        return handleResponse(type, id, request);
    }

    public boolean consume(String type, Long id, int count) throws UnknownUserIdException, UnknownBucketTypeException, InsufficientBucketTokensException, EmailNotConfirmedException, UserBlockedException, ServerException {
        HttpRequest request = HttpRequest.get(getUrl("/bucket/consume/" + type + "/" + id + "/" + count));
        return handleResponse(type, id, request);
    }

    private String getUrl(String actionUrl) {
        return BASE_URL + actionUrl;
    }

    protected boolean handleResponse(String type, Long id, HttpRequest request) throws UnknownUserIdException, UnknownBucketTypeException, InsufficientBucketTokensException, EmailNotConfirmedException, UserBlockedException, ServerException {
        try {
            //        int responseCode = request.code();
//        System.out.println("Response code: " + responseCode);
            String response = request.body();
//        System.out.println(response);
            JsonElement jelement = new JsonParser().parse(response);
            JsonObject responseObject = jelement.getAsJsonObject();
            if (responseObject.get("success").getAsBoolean()) {
                return true;
            } else {
                String message = responseObject.get("message").getAsString();
                switch (responseObject.get("code").getAsInt()) {
                    case 400:
                        throw new UnknownUserIdException(id, message);
                    case 420:
                        throw new UserBlockedException(message);
                    case 430:
                        throw new EmailNotConfirmedException(message);
                    case 470:
                        String nextTime = responseObject.get("data").getAsString();
                        throw new InsufficientBucketTokensException(message, nextTime);
                    case 480:
                        throw new UnknownBucketTypeException(type, message);
                }
                return false;
            }
        } catch (HttpRequest.HttpRequestException hre) {
            LOG.error("Inter service error", hre);
            throw new ServerException(hre);
        }
    }

}
