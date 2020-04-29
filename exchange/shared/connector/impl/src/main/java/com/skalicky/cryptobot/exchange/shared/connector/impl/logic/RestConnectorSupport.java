/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2020 Tomas Skalicky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.skalicky.cryptobot.exchange.shared.connector.impl.logic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

public class RestConnectorSupport {

    @Nonnull
    private static final Logger logger = LoggerFactory.getLogger(RestConnectorSupport.class);
    @Nonnull
    private static final Client client = ClientBuilder.newClient(new ClientConfig());
    @Nonnull
    private final ObjectMapper objectMapper;

    public RestConnectorSupport(@Nonnull final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nonnull
    public <RequestPayload, ResponsePayload> ResponsePayload getAcceptingJson(@Nonnull URI uri,
                                                                              @Nonnull ImmutableMultivaluedMap<String, Object> requestHeaders,
                                                                              @Nonnull TypeReference<ResponsePayload> responseTypeReference) {
        return sendJsonAcceptingJson(HttpMethod.GET, uri, requestHeaders, null, responseTypeReference);
    }

    @Nonnull
    public <RequestPayload, ResponsePayload> ResponsePayload postJsonAcceptingJson(@Nonnull URI uri,
                                                                                   @Nonnull ImmutableMultivaluedMap<String, Object> requestHeaders,
                                                                                   @Nullable RequestPayload requestPayload,
                                                                                   @Nonnull TypeReference<ResponsePayload> responseTypeReference) {
        return sendJsonAcceptingJson(HttpMethod.POST, uri, requestHeaders, requestPayload, responseTypeReference);
    }

    @Nonnull
    private <RequestPayload, ResponsePayload> ResponsePayload sendJsonAcceptingJson(@Nonnull final String httpMethod,
                                                                                    @Nonnull final URI uri,
                                                                                    @Nonnull final ImmutableMultivaluedMap<String, Object> requestHeaders,
                                                                                    @Nullable final RequestPayload requestPayload,
                                                                                    @Nonnull TypeReference<ResponsePayload> responseTypeReference) {

        final Response response = sendJson(httpMethod, uri, requestHeaders, requestPayload);
        final Response.StatusType responseStatusInfo = response.getStatusInfo();
        final String responsePayloadString = response.readEntity(String.class);
        logRawResponse(uri, responsePayloadString);

        if (responseStatusInfo.getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new IllegalStateException("Unexpected response status code [" + responseStatusInfo.getStatusCode() + "]");
        }

        final String normalizedResponsePayloadString = normalizeResponseBodyString(responsePayloadString,
                responseTypeReference);

        try {
            return objectMapper.readValue(normalizedResponsePayloadString,
                    responseTypeReference);

        } catch (@Nonnull final JsonProcessingException ex) {
            final String message = "Snapshot of data: responsePayloadString = " + responsePayloadString
                    + ", normalizedResponseBodyString = " + normalizedResponsePayloadString + "";
            logger.error(message);
            throw new IllegalStateException(ex.getMessage() + " " + message, ex);
        }
    }

    @Nonnull
    private <RequestPayload> Response sendJson(@Nonnull final String httpMethod,
                                               @Nonnull final URI uri,
                                               @Nonnull final ImmutableMultivaluedMap<String, Object> requestHeaders,
                                               @Nullable final RequestPayload requestPayload) {
        final WebTarget webTarget = client.target(uri);

        final Invocation.Builder invocationBuilder = webTarget.request().headers(requestHeaders);
        final Entity<RequestPayload> requestEntity = Entity.entity(requestPayload, MediaType.APPLICATION_JSON);

        logger.debug("URI {} - Request: {}", uri, requestEntity);

        return invocationBuilder.method(httpMethod, requestEntity);
    }

    private void logRawResponse(@Nonnull final URI uri,
                                @Nonnull final String responseString) {
        final var maxLength = 512;
        final boolean showDots = responseString.length() > maxLength;
        logger.debug("URI {} - Raw Response: {}{}", uri,
                responseString.substring(0, Math.min(maxLength, responseString.length())), showDots ? "..." : "");
    }

    @Nullable
    private <T> String normalizeResponseBodyString(@Nullable String responseString,
                                                   @Nonnull TypeReference<T> responseTypeReference) {
        if (responseTypeReference.getType() == String.class && responseString != null && !responseString.isEmpty()) {
            return '\"' + responseString + '\"';
        } else {
            return responseString;
        }
    }

}
