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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import edu.self.kraken.api.KrakenApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPublicApiConnectorImplUTest {
    @Nonnull
    private final KrakenApi krakenApi = Mockito.mock(KrakenApi.class);
    @Nonnull
    private final KrakenPublicApiConnectorImpl krakenPublicApiConnectorImpl = new KrakenPublicApiConnectorImpl(krakenApi, new ObjectMapper());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenApi);
        Mockito.reset(krakenApi);
    }

    @Test
    public void ticker_when_dataForPairInResponseFile_then_askPriceReturned_and_bidPriceReturned() throws Exception {

        final Path fileWithExpectedResponse = Path.of(getClass().getClassLoader().getResource("com/skalicky/cryptobot/exchange/kraken/connector/impl/logic/ticker_pair_XBTEUR_response.json").toURI());
        final List<String> expectedResponseLines = Files.readAllLines(fileWithExpectedResponse);
        final String expectedResponse = String.join(System.lineSeparator(), expectedResponseLines);
        final Map<String, String> pair = Collections.singletonMap("pair", "XBTEUR");
        when(krakenApi.queryPublic(KrakenApi.Method.TICKER, pair)).thenReturn(expectedResponse);

        final KrakenResponseDto<Map<String, Map<String, Object>>> response = krakenPublicApiConnectorImpl.ticker(Collections.singletonList("XBTEUR"));

        verify(krakenApi).queryPublic(KrakenApi.Method.TICKER, pair);

        assertThat(response.getError()).isEmpty();
        assertThat(response.getResult()).hasSize(1);
        assertThat(((List<String>) response.getResult().get("XXBTZEUR").get("a")).get(0)).isEqualTo("8903.30000");
        assertThat(((List<String>) response.getResult().get("XXBTZEUR").get("b")).get(0)).isEqualTo("8902.40000");
    }
}
