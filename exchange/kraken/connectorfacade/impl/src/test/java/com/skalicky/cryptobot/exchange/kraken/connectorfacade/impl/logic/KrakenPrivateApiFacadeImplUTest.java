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

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenCurrencyNameToCurrencyBoEnumConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPrivateApiFacadeImplUTest {
    @Nonnull
    private final KrakenPrivateApiConnector krakenPrivateApiConnector = mock(KrakenPrivateApiConnector.class);
    @Nonnull
    private final KrakenPrivateApiFacadeImpl krakenPrivateApiFacadeImpl = new KrakenPrivateApiFacadeImpl(krakenPrivateApiConnector,
            new KrakenCurrencyNameToCurrencyBoEnumConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenPrivateApiConnector);
        Mockito.reset(krakenPrivateApiConnector);
    }

    @Test
    public void test_getAccountBalance_when_rawKrakenDataProvided_then_askPriceReturned_and_bidPriceReturned() {

        final Map<String, BigDecimal> balancesByCurrencies = Map.of("BCH", BigDecimal.ZERO, "ZEUR", new BigDecimal("34"), "XXRP", new BigDecimal("32"));
        final KrakenResponseDto<Map<String, BigDecimal>> expectedResponse = new KrakenResponseDto<>();
        expectedResponse.setResult(balancesByCurrencies);
        when(krakenPrivateApiConnector.getBalance()).thenReturn(expectedResponse);

        final Map<CurrencyBoEnum, BigDecimal> response = krakenPrivateApiFacadeImpl.getAccountBalance();

        verify(krakenPrivateApiConnector).getBalance();

        assertThat(response.get(CurrencyBoEnum.EUR)).isEqualTo(new BigDecimal(34));
        assertThat(response.containsKey(CurrencyBoEnum.BTC)).isFalse();
        assertThat(response.containsKey(CurrencyBoEnum.OTHERS)).isFalse();
    }
}
