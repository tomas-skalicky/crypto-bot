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

package com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic;

import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.CurrencyPairBoToKrakenMarketNameConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToTickerBoConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

public class KrakenPublicApiFacadeImplUTest {
    @NotNull
    private final KrakenPublicApiConnector krakenPublicApiConnector = mock(KrakenPublicApiConnector.class);
    @NotNull
    private final KrakenPublicApiFacadeImpl krakenPublicApiFacadeImpl = new KrakenPublicApiFacadeImpl(
            krakenPublicApiConnector,
            new CurrencyPairBoToKrakenMarketNameConverter(),
            new KrakenMapEntryToTickerBoConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        verifyNoMoreInteractions(krakenPublicApiConnector);
        reset(krakenPublicApiConnector);
    }

    @Test
    public void test_getTicker_when_rawKrakenDataProvided_then_askPriceReturned_and_bidPriceReturned() {

        // Given
        final Map<String, Object> pairData = Map.of("a", List.of("8903.300000"), "b", List.of("8902.400000"));
        final var tickerName = "XXBTZEUR";
        final var result = Map.of(tickerName, pairData);
        final var expectedResponse = new KrakenResponseDto<Map<String, Map<String, Object>>>();
        expectedResponse.setResult(result);
        final var marketName = "XBTEUR";
        final var marketNames = ImmutableList.of(marketName);
        given(krakenPublicApiConnector.ticker(marketNames)).willReturn(expectedResponse);
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

        // When
        final TickerBo response = krakenPublicApiFacadeImpl.getTicker(currencyPair);

        // Then
        verify(krakenPublicApiConnector).ticker(marketNames);

        then(response.getTickerName()).isEqualTo(tickerName);
        then(response.getAskPrice().stripTrailingZeros()).isEqualTo(new BigDecimal("8903.3").stripTrailingZeros());
        then(response.getBidPrice().stripTrailingZeros()).isEqualTo(new BigDecimal("8902.4").stripTrailingZeros());
    }
}
