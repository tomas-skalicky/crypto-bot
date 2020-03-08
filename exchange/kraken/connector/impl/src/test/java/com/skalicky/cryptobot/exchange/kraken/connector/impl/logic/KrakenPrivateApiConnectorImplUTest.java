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
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
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

        // @formatter:off
        final String expectedResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"ZEUR\": \"100.7896\"," +
                "        \"XXBT\": \"0.0000000030\"," +
                "        \"BCH\":  \"0.0000000000\"" +
                "    }" +
                "}";
        // @formatter:on
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

    @Test
    public void test_addOrder_when_everythingOk_then_noError() throws Exception {

        // @formatter:off
        final String expectedResponse = "{}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap())).thenReturn(expectedResponse);

        final KrakenResponseDto<Object> response = krakenPrivateApiConnectorImpl.addOrder(
                "LTCEUR", "buy", "market", new BigDecimal(40),
                BigDecimal.ONE, Collections.emptyList(), 1);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap());

        assertThat(response.getError()).isNull();
        assertThat(response.getResult()).isNull();
    }

    @Test
    public void test_addOrder_when_insufficientPermissions_then_correspondingError() throws Exception {

        // @formatter:off
        final String expectedResponse = "{" +
                "    \"error\": [\"EGeneral:Permission denied\"]" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap())).thenReturn(expectedResponse);

        final KrakenResponseDto<Object> response = krakenPrivateApiConnectorImpl.addOrder(
                "LTCEUR", "buy", "market", new BigDecimal(40),
                BigDecimal.ONE, Collections.emptyList(), 1);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap());

        assertThat(response.getError()).hasSize(1);
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(response.getError()).isNotNull();
        assertThat(response.getError().get(0)).isEqualTo("EGeneral:Permission denied");
        assertThat(response.getResult()).isNull();
    }
}
