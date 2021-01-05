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

package com.skalicky.cryptobot.exchange.kraken.connector.impl.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import edu.self.kraken.api.KrakenApi;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

public class KrakenPublicApiConnectorImplUTest {
    @NotNull
    private final KrakenApi krakenApi = mock(KrakenApi.class);
    @NotNull
    private final KrakenPublicApiConnectorImpl krakenPublicApiConnectorImpl = new KrakenPublicApiConnectorImpl(krakenApi, new ObjectMapper());

    @AfterEach
    public void assertAndCleanMocks() {
        verifyNoMoreInteractions(krakenApi);
        reset(krakenApi);
    }

    @Test
    public void test_ticker_when_responsePayloadIsValid_then_askPriceReturned_and_bidPriceReturned() throws Exception {

        // Given
        // @formatter:off
        final var expectedResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"XXBTZEUR\": {" +
                "            \"a\": [\"8903.30000\",   \"2\",            \"2.000\"]," +
                "            \"b\": [\"8902.40000\",   \"1\",            \"1.000\"]," +
                "            \"c\": [\"8902.70000\",   \"0.02808553\"]," +
                "            \"v\": [\"643.83215394\", \"1532.80579909\"]," +
                "            \"p\": [\"8904.53198\",   \"8926.98758\"]," +
                "            \"t\": [  6852,             14123]," +
                "            \"l\": [\"8829.80000\",   \"8804.00000\"]," +
                "            \"h\": [\"8959.40000\",   \"9008.90000\"]," +
                "            \"o\": \"8945.20000\"" +
                "        }" +
                "    }" +
                "}";
        // @formatter:on
        final var marketName = Collections.singletonMap("pair", "XBTEUR");
        given(krakenApi.queryPublic(KrakenApi.Method.TICKER, marketName)).willReturn(expectedResponse);

        // When
        final KrakenResponseDto<Map<String, Map<String, Object>>> response =
                krakenPublicApiConnectorImpl.ticker(ImmutableList.of("XBTEUR"));

        // Then
        verify(krakenApi).queryPublic(KrakenApi.Method.TICKER, marketName);

        then(response.getError()).isEmpty();
        then(response.getResult()).hasSize(1);
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(response.getResult()).isNotNull();
        final var tickerName = "XXBTZEUR";
        @SuppressWarnings("unchecked") final var actualAskData = (List<String>) response.getResult().get(tickerName).get("a");
        then(actualAskData.get(0)).isEqualTo("8903.30000");
        @SuppressWarnings("unchecked") final var actualBidData = (List<String>) response.getResult().get(tickerName).get("b");
        then(actualBidData.get(0)).isEqualTo("8902.40000");
    }
}
