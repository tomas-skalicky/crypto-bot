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
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPrivateApiConnectorImplUTest {
    @Nonnull
    private final KrakenApi krakenApi = Mockito.mock(KrakenApi.class);
    @Nonnull
    private final KrakenPrivateApiConnectorImpl krakenPrivateApiConnectorImpl = new KrakenPrivateApiConnectorImpl(krakenApi, new ObjectMapper());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenApi);
        Mockito.reset(krakenApi);
    }

    @Test
    public void test_getBalance_when_dataForPairInResponseFile_then_askPriceReturned_and_bidPriceReturned() throws Exception {

        final URL urlOfFileWithExpectedResponse = getClass().getClassLoader().getResource("com/skalicky/cryptobot/exchange/kraken/connector/impl/logic/getBalance_response.json");
        Objects.requireNonNull(urlOfFileWithExpectedResponse);
        final Path fileWithExpectedResponse = Path.of(urlOfFileWithExpectedResponse.toURI());
        final List<String> expectedResponseLines = Files.readAllLines(fileWithExpectedResponse);
        final String expectedResponse = String.join(System.lineSeparator(), expectedResponseLines);
        when(krakenApi.queryPrivate(KrakenApi.Method.BALANCE)).thenReturn(expectedResponse);

        final KrakenResponseDto<Map<String, BigDecimal>> response = krakenPrivateApiConnectorImpl.getBalance();

        verify(krakenApi).queryPrivate(KrakenApi.Method.BALANCE);

        assertThat(response.getError()).isEmpty();
        assertThat(response.getResult()).hasSize(3);
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().get("ZEUR")).isEqualTo(new BigDecimal("100.7896"));
        assertThat(response.getResult().get("XXBT")).isEqualTo(new BigDecimal("0.0000000030"));
        assertThat(response.getResult().get("BCH")).isEqualTo(new BigDecimal("0.0000000000"));
    }
}
