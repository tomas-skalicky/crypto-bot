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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RestConnectorSupportUTest {

    @NotNull
    private WireMockServer wireMockServer;
    @NotNull
    private final ObjectMapper objectMapper = new ObjectMapper();
    @NotNull
    private final RestConnectorSupport restConnectorSupport = new RestConnectorSupport(objectMapper);

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void test_getAcceptingJson_when_successHttpStatus_and_responsePayloadIsJsonDeserializable_then_responseIsDeserializedSuccessfully() throws Exception {
        final var testResponse = new TestResponse("valid");
        final String serializedResponse = objectMapper.writeValueAsString(testResponse);
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                get(urlEqualTo(endpoint))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(serializedResponse)));
        final int port = wireMockServer.port();
        final var uri = new URI("http://localhost:" + port + endpoint);
        final var responseTypeReference = new TypeReference<TestResponse>() {
        };

        final TestResponse actualResponse = restConnectorSupport.getAcceptingJson(uri,
                ImmutableMultivaluedMap.empty(), responseTypeReference);

        wireMockServer.verify(
                getRequestedFor(urlEqualTo(endpoint)));

        assertThat(actualResponse.getResult()).isEqualTo(testResponse.getResult());
    }

    @Test
    public void test_postJsonAcceptingJson_when_successHttpStatus_and_responsePayloadIsJsonDeserializable_then_responseIsDeserializedSuccessfully() throws Exception {
        final var testRequest = new TestRequest("Tomas Skalicky");
        final String serializedRequest = objectMapper.writeValueAsString(testRequest);
        final var testResponse = new TestResponse("valid");
        final String serializedResponse = objectMapper.writeValueAsString(testResponse);
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                post(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(serializedResponse)));
        final int port = wireMockServer.port();
        final var uri = new URI("http://localhost:" + port + endpoint);
        final var responseTypeReference = new TypeReference<TestResponse>() {
        };

        final TestResponse actualResponse = restConnectorSupport.postJsonAcceptingJson(uri,
                ImmutableMultivaluedMap.empty(), testRequest, responseTypeReference);

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest)));

        assertThat(actualResponse.getResult()).isEqualTo(testResponse.getResult());
    }

    @Test
    public void test_postJsonAcceptingJson_when_nonSuccessHttpStatus_then_exception() throws Exception {
        final var testRequest = new TestRequest("Tomas Skalicky");
        final String serializedRequest = objectMapper.writeValueAsString(testRequest);
        final String serializedResponse = objectMapper.writeValueAsString(new TestResponse("redirection"));
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                post(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest))
                        .willReturn(aResponse()
                                .withStatus(300)
                                .withBody(serializedResponse)));
        final int port = wireMockServer.port();
        final var uri = new URI("http://localhost:" + port + endpoint);
        final var responseTypeReference = new TypeReference<TestResponse>() {
        };

        assertThatThrownBy(() -> restConnectorSupport.postJsonAcceptingJson(uri, ImmutableMultivaluedMap.empty(),
                testRequest, responseTypeReference))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Unexpected response status code [300]");

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest)));
    }

    @Test
    public void test_postJsonAcceptingJson_when_responsePayloadIsString_then_responsePayloadIsNormalized() throws Exception {
        final var testRequest = new TestRequest("Tomas Skalicky");
        final String serializedRequest = objectMapper.writeValueAsString(testRequest);
        final String response = "test response";
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                post(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(response)));
        final int port = wireMockServer.port();
        final var uri = new URI("http://localhost:" + port + endpoint);
        final var responseTypeReference = new TypeReference<String>() {
        };

        final String actualResponse = restConnectorSupport.postJsonAcceptingJson(uri,
                ImmutableMultivaluedMap.empty(), testRequest, responseTypeReference);

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest)));

        assertThat(actualResponse).isEqualTo(response);
    }

    @Test
    public void test_postJsonAcceptingJson_when_responsePayloadIsNotJsonDeserializable_then_exception() throws Exception {
        final var testRequest = new TestRequest("Tomas Skalicky");
        final String serializedRequest = objectMapper.writeValueAsString(testRequest);
        final String serializedResponse = "{ \"nonExistingField\": \"Foo\" }";
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                post(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(serializedResponse)));
        final int port = wireMockServer.port();
        final var uri = new URI("http://localhost:" + port + endpoint);
        final var responseTypeReference = new TypeReference<TestResponse>() {
        };

        assertThatThrownBy(() -> restConnectorSupport.postJsonAcceptingJson(uri, ImmutableMultivaluedMap.empty(),
                testRequest, responseTypeReference))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageEndingWith("Snapshot of data: responsePayloadString = { \"nonExistingField\": \"Foo\" }," +
                        " normalizedResponseBodyString = { \"nonExistingField\": \"Foo\" }");

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest)));
    }

    private static final class TestRequest {
        @NotNull
        private final String name;

        private TestRequest(@NotNull final String name) {
            this.name = name;
        }

        @NotNull
        public String getName() {
            return name;
        }
    }

    private static final class TestResponse {
        /**
         * Initialized to avoid nullability.
         */
        @NotNull
        private String result = "";

        /**
         * Due to jackson deserialization.
         */
        public TestResponse() {
        }

        public TestResponse(@NotNull final String result) {
            this.result = result;
        }

        @NotNull
        public String getResult() {
            return result;
        }

        public void setResult(@NotNull final String result) {
            this.result = result;
        }
    }
}
