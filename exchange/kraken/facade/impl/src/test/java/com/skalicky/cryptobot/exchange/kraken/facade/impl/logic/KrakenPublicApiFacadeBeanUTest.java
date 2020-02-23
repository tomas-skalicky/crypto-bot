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

package com.skalicky.cryptobot.exchange.kraken.facade.impl.logic;

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import com.skalicky.cryptobot.exchange.kraken.facade.impl.converter.KrakenMapEntryToTickerBoConverter;
import com.skalicky.cryptobot.exchange.shared.facade.api.bo.TickerBo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPublicApiFacadeBeanUTest {
    @Nonnull
    private final KrakenPublicApiConnector krakenPublicApiConnector = mock(KrakenPublicApiConnector.class);
    @Nonnull
    private final KrakenPublicApiFacadeBean krakenPublicApiFacadeBean = new KrakenPublicApiFacadeBean(krakenPublicApiConnector,
            new KrakenMapEntryToTickerBoConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenPublicApiConnector);
        Mockito.reset(krakenPublicApiConnector);
    }

    @Test
    public void getTicker_when_rawKrakenDataProvided_then_askPriceReturned_and_bidPriceReturned() {

        final Map<String, Object> pairData = Map.of("a", List.of("8903.300000"), "b", List.of("8902.400000"));
        final Map<String, Map<String, Object>> result = Map.of("XXBTZEUR", pairData);
        final KrakenResponseDto<Map<String, Map<String, Object>>> expectedResponse = new KrakenResponseDto<>();
        expectedResponse.setResult(result);
        final String marketName = "XBTEUR";
        final List<String> marketNames = Collections.singletonList(marketName);
        when(krakenPublicApiConnector.ticker(marketNames)).thenReturn(expectedResponse);

        final TickerBo response = krakenPublicApiFacadeBean.getTicker(marketName);

        verify(krakenPublicApiConnector).ticker(marketNames);

        assertThat(response.getMarketName()).isEqualTo("XXBTZEUR");
        assertThat(response.getAskPrice().stripTrailingZeros()).isEqualTo(new BigDecimal("8903.3").stripTrailingZeros());
        assertThat(response.getBidPrice().stripTrailingZeros()).isEqualTo(new BigDecimal("8902.4").stripTrailingZeros());
    }
}
