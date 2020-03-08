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

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenAddOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenCurrencyNameToCurrencyBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.OrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PairOfQuoteAndBaseCurrencyBoEnumToKrakenInputMarketNameConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PriceOrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPrivateApiFacadeImplUTest {
    @Nonnull
    private final KrakenPrivateApiConnector krakenPrivateApiConnector = mock(KrakenPrivateApiConnector.class);
    @Nonnull
    private final KrakenPrivateApiFacadeImpl krakenPrivateApiFacadeImpl = new KrakenPrivateApiFacadeImpl(
            krakenPrivateApiConnector,
            new PairOfQuoteAndBaseCurrencyBoEnumToKrakenInputMarketNameConverter(),
            new OrderTypeBoEnumToKrakenOrderTypeConverter(),
            new PriceOrderTypeBoEnumToKrakenOrderTypeConverter(),
            new KrakenCurrencyNameToCurrencyBoEnumConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenPrivateApiConnector);
        Mockito.reset(krakenPrivateApiConnector);
    }

    @Test
    public void test_getAccountBalance_when_rawKrakenDataProvided_then_askPriceReturned_and_bidPriceReturned() {

        final Map<String, BigDecimal> balancesByCurrencies = Map.of("BCH", BigDecimal.ZERO,
                "ZEUR", new BigDecimal("34"), "XXRP", new BigDecimal("32"));
        final KrakenResponseDto<Map<String, BigDecimal>> expectedResponse = new KrakenResponseDto<>();
        expectedResponse.setResult(balancesByCurrencies);
        when(krakenPrivateApiConnector.getBalance()).thenReturn(expectedResponse);

        final Map<CurrencyBoEnum, BigDecimal> response = krakenPrivateApiFacadeImpl.getAccountBalance();

        verify(krakenPrivateApiConnector).getBalance();

        assertThat(response.get(CurrencyBoEnum.EUR)).isEqualTo(new BigDecimal(34));
        assertThat(response.containsKey(CurrencyBoEnum.BTC)).isFalse();
        assertThat(response.containsKey(CurrencyBoEnum.OTHERS)).isFalse();
    }

    @Test
    public void test_placeOrder_when_krakenResponseWithoutErrors_then_noException() {

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal price = new BigDecimal("7000.3");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final List<String> orderFlags = Collections.emptyList();
        final long orderExpirationInSecondsFromNow = 0;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());

        krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, CurrencyBoEnum.EUR, CurrencyBoEnum.BTC, volumeInQuoteCurrency,
                price, false,
                orderExpirationInSecondsFromNow);

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

    @Test
    public void test_placeOrder_when_preferFeeInQuoteCurrencyIsTrue_then_fciqInOrderFlags() {

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal price = new BigDecimal("7000.3");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final List<String> orderFlags = List.of("fciq");
        final long orderExpirationInSecondsFromNow = 0;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());

        krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, CurrencyBoEnum.EUR, CurrencyBoEnum.BTC, volumeInQuoteCurrency,
                price, true,
                orderExpirationInSecondsFromNow);

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

    @Test
    public void test_placeOrder_when_priceHasMoreThanOneDecimalPlace_then_priceRoundedToOneDecimalPlace() {

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal krakenPrice = new BigDecimal("7000.4");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final List<String> orderFlags = Collections.emptyList();
        final long orderExpirationInSecondsFromNow = 0;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, krakenPrice, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());

        krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, CurrencyBoEnum.EUR, CurrencyBoEnum.BTC, volumeInQuoteCurrency,
                new BigDecimal("7000.351"), false,
                orderExpirationInSecondsFromNow);

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, krakenPrice, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

    @Test
    public void test_placeOrder_when_krakenResponseWithError_then_exception() {

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal price = new BigDecimal("7000.3");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final List<String> orderFlags = Collections.emptyList();
        final long orderExpirationInSecondsFromNow = 0;
        final KrakenResponseDto<KrakenAddOrderResultDto> response = new KrakenResponseDto<>();
        response.setError(List.of("Kraken error"));
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(response);

        assertThatThrownBy(() -> krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, CurrencyBoEnum.EUR, CurrencyBoEnum.BTC, volumeInQuoteCurrency,
                price, false,
                orderExpirationInSecondsFromNow))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Kraken error]");

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

}
