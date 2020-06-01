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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenAddOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDtoBuilder;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDtoBuilder;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.CurrencyPairBoToKrakenMarketNameConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenCurrencyNameToCurrencyBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToClosedOrderBoConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToOpenOrderBoConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMarketNameToCurrencyPairBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToPriceOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.OrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PriceOrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.EpochSecondBigDecimalToLocalDateTimeConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.LocalDateTimeToEpochSecondLongConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPrivateApiFacadeImplUTest {
    @NotNull
    private final KrakenPrivateApiConnector krakenPrivateApiConnector = mock(KrakenPrivateApiConnector.class);
    @NotNull
    private final KrakenPrivateApiFacadeImpl krakenPrivateApiFacadeImpl = new KrakenPrivateApiFacadeImpl(
            krakenPrivateApiConnector,
            new CurrencyPairBoToKrakenMarketNameConverter(),
            new OrderTypeBoEnumToKrakenOrderTypeConverter(),
            new PriceOrderTypeBoEnumToKrakenOrderTypeConverter(),
            new KrakenCurrencyNameToCurrencyBoEnumConverter(),
            new KrakenMapEntryToOpenOrderBoConverter(
                    new KrakenOrderTypeToOrderTypeBoEnumConverter(),
                    new KrakenOrderTypeToPriceOrderTypeBoEnumConverter(),
                    new KrakenMarketNameToCurrencyPairBoEnumConverter(),
                    new EpochSecondBigDecimalToLocalDateTimeConverter(),
                    new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter()
            ),
            new KrakenMapEntryToClosedOrderBoConverter(
                    new KrakenOrderTypeToOrderTypeBoEnumConverter(),
                    new KrakenOrderTypeToPriceOrderTypeBoEnumConverter(),
                    new KrakenMarketNameToCurrencyPairBoEnumConverter(),
                    new EpochSecondBigDecimalToLocalDateTimeConverter(),
                    new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter()
            ),
            new LocalDateTimeToEpochSecondLongConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenPrivateApiConnector);
        Mockito.reset(krakenPrivateApiConnector);
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithError_then_exception() {

        final var krakenResponseDto = new KrakenResponseDto<KrakenOpenOrderResultDto>();
        krakenResponseDto.setError(List.of("Error 12343"));
        final var includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        assertThatThrownBy(() -> krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Error 12343]");

        verify(krakenPrivateApiConnector).openOrders(includeTrades);
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithNullResult_then_emptyListIsReturned() {

        final var krakenResponseDto = new KrakenResponseDto<KrakenOpenOrderResultDto>();
        krakenResponseDto.setResult(null);
        final var includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        final ImmutableList<OpenOrderBo> openOrders = krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades);

        verify(krakenPrivateApiConnector).openOrders(includeTrades);

        assertThat(openOrders).isEmpty();
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithEmptyOpen_then_emptyListIsReturned() {

        final var result = new KrakenOpenOrderResultDto();
        result.setOpen(null);
        final var krakenResponseDto = new KrakenResponseDto<KrakenOpenOrderResultDto>();
        krakenResponseDto.setResult(result);
        final var includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        final ImmutableList<OpenOrderBo> openOrders = krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades);

        verify(krakenPrivateApiConnector).openOrders(includeTrades);

        assertThat(openOrders).isEmpty();
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithOneOpenOrder_then_oneOrderReturned() {

        final KrakenOpenOrderDto order = KrakenOpenOrderDtoBuilder.aKrakenOpenOrderDto().build();
        final var result = new KrakenOpenOrderResultDto();
        final var orderId = "orderId1";
        result.setOpen(Map.of(orderId, order));
        final var krakenResponseDto = new KrakenResponseDto<KrakenOpenOrderResultDto>();
        krakenResponseDto.setResult(result);
        final var includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        final ImmutableList<OpenOrderBo> openOrders = krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades);

        verify(krakenPrivateApiConnector).openOrders(includeTrades);

        assertThat(openOrders).hasSize(1);
        assertThat(openOrders.get(0).getOrderId()).isEqualTo(orderId);
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithError_then_exception() {

        final var krakenResponseDto = new KrakenResponseDto<KrakenClosedOrderResultDto>();
        krakenResponseDto.setError(List.of("Error 123"));
        final var includeTrades = true;
        final var from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final var fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        assertThatThrownBy(() -> krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades, from))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Error 123]");

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithNullResult_then_emptyListIsReturned() {

        final var krakenResponseDto = new KrakenResponseDto<KrakenClosedOrderResultDto>();
        krakenResponseDto.setResult(null);
        final var includeTrades = true;
        final var from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final var fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        final ImmutableList<ClosedOrderBo> closedOrders = krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades,
                from);

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);

        assertThat(closedOrders).isEmpty();
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithEmptyClosed_then_emptyListIsReturned() {

        final var result = new KrakenClosedOrderResultDto();
        result.setClosed(null);
        final var krakenResponseDto = new KrakenResponseDto<KrakenClosedOrderResultDto>();
        krakenResponseDto.setResult(result);
        final var includeTrades = true;
        final var from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final var fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        final ImmutableList<ClosedOrderBo> closedOrders = krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades,
                from);

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);

        assertThat(closedOrders).isEmpty();
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithOneClosedOrder_then_oneOrderReturned() {

        final KrakenClosedOrderDto order = KrakenClosedOrderDtoBuilder.aKrakenClosedOrderDto().build();
        final var result = new KrakenClosedOrderResultDto();
        final var orderId = "orderId1";
        result.setClosed(Map.of(orderId, order));
        final var krakenResponseDto = new KrakenResponseDto<KrakenClosedOrderResultDto>();
        krakenResponseDto.setResult(result);
        final var includeTrades = true;
        final var from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final var fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        final ImmutableList<ClosedOrderBo> closedOrders = krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades,
                from);

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);

        assertThat(closedOrders).hasSize(1);
        assertThat(closedOrders.get(0).getOrderId()).isEqualTo(orderId);
    }

    @Test
    public void test_getAccountBalance_when_rawKrakenDataProvided_then_askPriceReturned_and_bidPriceReturned() {

        final var balancesByCurrencies = Map.of("BCH", BigDecimal.ZERO,
                "ZEUR", new BigDecimal("34"), "XXRP", new BigDecimal("32"));
        final var krakenResponseDto = new KrakenResponseDto<Map<String, BigDecimal>>();
        krakenResponseDto.setResult(balancesByCurrencies);
        when(krakenPrivateApiConnector.balance()).thenReturn(krakenResponseDto);

        final Map<CurrencyBoEnum, BigDecimal> response = krakenPrivateApiFacadeImpl.getAccountBalance();

        verify(krakenPrivateApiConnector).balance();

        assertThat(response.get(CurrencyBoEnum.EUR)).isEqualTo(new BigDecimal(34));
        assertThat(response.containsKey(CurrencyBoEnum.BTC)).isFalse();
        assertThat(response.containsKey(CurrencyBoEnum.OTHERS)).isFalse();
    }

    @Test
    public void test_placeOrder_when_krakenResponseWithoutErrors_then_noException() {

        final var krakenMarketName = "XBTEUR";
        final var krakenOrderType = "buy";
        final var krakenPriceOrderType = "market";
        final var price = new BigDecimal("7000.3");
        final var volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.of();
        final var orderExpirationInSecondsFromNow = 0L;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

        krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, currencyPair, volumeInQuoteCurrency,
                price, false,
                orderExpirationInSecondsFromNow);

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

    @Test
    public void test_placeOrder_when_preferFeeInQuoteCurrencyIsTrue_then_fciqInOrderFlags() {

        final var krakenMarketName = "XBTEUR";
        final var krakenOrderType = "buy";
        final var krakenPriceOrderType = "market";
        final var price = new BigDecimal("7000.3");
        final var volumeInQuoteCurrency = new BigDecimal("0.01");
        final var orderFlags = ImmutableList.of("fciq");
        final var orderExpirationInSecondsFromNow = 0L;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

        krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, currencyPair, volumeInQuoteCurrency,
                price, true,
                orderExpirationInSecondsFromNow);

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

    @Test
    public void test_placeOrder_when_priceHasMoreThanOneDecimalPlace_then_priceRoundedToOneDecimalPlace() {

        final var krakenMarketName = "XBTEUR";
        final var krakenOrderType = "buy";
        final var krakenPriceOrderType = "market";
        final var krakenPrice = new BigDecimal("7000.4");
        final var volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.of();
        final var orderExpirationInSecondsFromNow = 0L;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, krakenPrice, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

        krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, currencyPair, volumeInQuoteCurrency,
                new BigDecimal("7000.351"), false,
                orderExpirationInSecondsFromNow);

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, krakenPrice, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

    @Test
    public void test_placeOrder_when_krakenResponseWithError_then_exception() {

        final var krakenMarketName = "XBTEUR";
        final var krakenOrderType = "buy";
        final var krakenPriceOrderType = "market";
        final var price = new BigDecimal("7000.3");
        final var volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.of();
        final var orderExpirationInSecondsFromNow = 0L;
        final var krakenResponseDto = new KrakenResponseDto<KrakenAddOrderResultDto>();
        krakenResponseDto.setError(List.of("Kraken error"));
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(krakenResponseDto);
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

        assertThatThrownBy(() -> krakenPrivateApiFacadeImpl.placeOrder(OrderTypeBoEnum.BUY,
                PriceOrderTypeBoEnum.MARKET, currencyPair, volumeInQuoteCurrency,
                price, false,
                orderExpirationInSecondsFromNow))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Kraken error]");

        verify(krakenPrivateApiConnector).addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow);
    }

}
