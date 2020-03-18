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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RestConnectorSupportUTest {

    @Nonnull
    private WireMockServer wireMockServer;
    @Nonnull
    private ObjectMapper objectMapper = new ObjectMapper();
    @Nonnull
    private RestConnectorSupport restConnectorSupport = new RestConnectorSupport();

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
    public void test_postJson_when_successHttpStatus_then_noException() throws Exception {
        final var testRequest = new TestRequest("Tomas Skalicky");
        final var serializedRequest = objectMapper.writeValueAsString(testRequest);
        final var testResponse = new TestResponse("no problem");
        final var serializedResponse = objectMapper.writeValueAsString(testResponse);
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                post(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody(serializedResponse)));
        final var port = wireMockServer.port();

        restConnectorSupport.postJson(testRequest, "http://localhost:" + port + endpoint);

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest)));
    }

    @Test
    public void test_postJson_when_nonSuccessHttpStatus_then_exception() throws Exception {
        final var testRequest = new TestRequest("Tomas Skalicky");
        final var serializedRequest = objectMapper.writeValueAsString(testRequest);
        final var testResponse = new TestResponse("redirection to a new URL");
        final var serializedResponse = objectMapper.writeValueAsString(testResponse);
        final var endpoint = "/context-path/servlet-mapping/endpoint";
        wireMockServer.stubFor(
                post(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest))
                        .willReturn(aResponse()
                                .withStatus(300)
                                .withBody(serializedResponse)));
        final var port = wireMockServer.port();

        assertThatThrownBy(() -> restConnectorSupport.postJson(testRequest, "http://localhost:" + port + endpoint))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Unexpected response status. Response: ");

        wireMockServer.verify(
                postRequestedFor(urlEqualTo(endpoint))
                        .withHeader("Content-Type", equalTo("application/json"))
                        .withRequestBody(equalToJson(serializedRequest)));
    }

    private static final class TestRequest {
        @Nonnull
        private final String name;

        private TestRequest(@Nonnull final String name) {
            this.name = name;
        }

        @Nonnull
        public String getName() {
            return name;
        }
    }

    private static final class TestResponse {
        /**
         * Initialized to avoid nullability.
         */
        @Nonnull
        private String result = "";

        /**
         * Due to jackson deserialization.
         */
        public TestResponse() {
        }

        public TestResponse(@Nonnull final String result) {
            this.result = result;
        }

        @Nonnull
        public String getResult() {
            return result;
        }

        public void setResult(@Nonnull final String result) {
            this.result = result;
        }
    }
}
