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
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderStatusToOrderStateBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToPriceOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.OrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PriceOrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.EpochSecondBigDecimalToLocalDateTimeConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.LocalDateTimeToEpochSecondLongConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
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
    @Nonnull
    private final KrakenPrivateApiConnector krakenPrivateApiConnector = mock(KrakenPrivateApiConnector.class);
    @Nonnull
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
                    new KrakenOrderStatusToOrderStateBoEnumConverter()
            ),
            new KrakenMapEntryToClosedOrderBoConverter(
                    new KrakenOrderTypeToOrderTypeBoEnumConverter(),
                    new KrakenOrderTypeToPriceOrderTypeBoEnumConverter(),
                    new KrakenMarketNameToCurrencyPairBoEnumConverter(),
                    new EpochSecondBigDecimalToLocalDateTimeConverter(),
                    new KrakenOrderStatusToOrderStateBoEnumConverter()
            ),
            new LocalDateTimeToEpochSecondLongConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenPrivateApiConnector);
        Mockito.reset(krakenPrivateApiConnector);
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithError_then_exception() {

        final KrakenResponseDto<KrakenOpenOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setError(List.of("Error 12343"));
        final boolean includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        assertThatThrownBy(() -> krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Error 12343]");

        verify(krakenPrivateApiConnector).openOrders(includeTrades);
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithNullResult_then_emptyListIsReturned() {

        final KrakenResponseDto<KrakenOpenOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setResult(null);
        final boolean includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        final ImmutableList<OpenOrderBo> openOrders = krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades);

        verify(krakenPrivateApiConnector).openOrders(includeTrades);

        assertThat(openOrders).isEmpty();
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithEmptyOpen_then_emptyListIsReturned() {

        final KrakenOpenOrderResultDto result = new KrakenOpenOrderResultDto();
        result.setOpen(null);
        final KrakenResponseDto<KrakenOpenOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setResult(result);
        final boolean includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        final ImmutableList<OpenOrderBo> openOrders = krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades);

        verify(krakenPrivateApiConnector).openOrders(includeTrades);

        assertThat(openOrders).isEmpty();
    }

    @Test
    public void test_getOpenOrders_when_krakenResponseWithOneOpenOrder_then_oneOrderReturned() {

        final KrakenOpenOrderDto order = KrakenOpenOrderDtoBuilder.aKrakenOpenOrderDto().build();
        final KrakenOpenOrderResultDto result = new KrakenOpenOrderResultDto();
        final String orderId = "orderId1";
        result.setOpen(Map.of(orderId, order));
        final KrakenResponseDto<KrakenOpenOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setResult(result);
        final boolean includeTrades = true;
        when(krakenPrivateApiConnector.openOrders(includeTrades)).thenReturn(krakenResponseDto);

        final ImmutableList<OpenOrderBo> openOrders = krakenPrivateApiFacadeImpl.getOpenOrders(includeTrades);

        verify(krakenPrivateApiConnector).openOrders(includeTrades);

        assertThat(openOrders).hasSize(1);
        assertThat(openOrders.get(0).getOrderId()).isEqualTo(orderId);
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithError_then_exception() {

        final KrakenResponseDto<KrakenClosedOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setError(List.of("Error 123"));
        final boolean includeTrades = true;
        final LocalDateTime from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final Long fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        assertThatThrownBy(() -> krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades, from))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("[Error 123]");

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithNullResult_then_emptyListIsReturned() {

        final KrakenResponseDto<KrakenClosedOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setResult(null);
        final boolean includeTrades = true;
        final LocalDateTime from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final Long fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        final ImmutableList<ClosedOrderBo> closedOrders = krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades,
                from);

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);

        assertThat(closedOrders).isEmpty();
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithEmptyClosed_then_emptyListIsReturned() {

        final KrakenClosedOrderResultDto result = new KrakenClosedOrderResultDto();
        result.setClosed(null);
        final KrakenResponseDto<KrakenClosedOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setResult(result);
        final boolean includeTrades = true;
        final LocalDateTime from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final Long fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        final ImmutableList<ClosedOrderBo> closedOrders = krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades,
                from);

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);

        assertThat(closedOrders).isEmpty();
    }

    @Test
    public void test_getClosedOrders_when_krakenResponseWithOneClosedOrder_then_oneOrderReturned() {

        final KrakenClosedOrderDto order = KrakenClosedOrderDtoBuilder.aKrakenClosedOrderDto().build();
        final KrakenClosedOrderResultDto result = new KrakenClosedOrderResultDto();
        final String orderId = "orderId1";
        result.setClosed(Map.of(orderId, order));
        final KrakenResponseDto<KrakenClosedOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setResult(result);
        final boolean includeTrades = true;
        final LocalDateTime from = LocalDateTime.of(2020, 3, 10, 10, 5);
        final Long fromInEpochSeconds = 1583831100L;
        when(krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds)).thenReturn(krakenResponseDto);

        final ImmutableList<ClosedOrderBo> closedOrders = krakenPrivateApiFacadeImpl.getClosedOrders(includeTrades,
                from);

        verify(krakenPrivateApiConnector).closedOrders(includeTrades, fromInEpochSeconds);

        assertThat(closedOrders).hasSize(1);
        assertThat(closedOrders.get(0).getOrderId()).isEqualTo(orderId);
    }

    @Test
    public void test_getAccountBalance_when_rawKrakenDataProvided_then_askPriceReturned_and_bidPriceReturned() {

        final Map<String, BigDecimal> balancesByCurrencies = Map.of("BCH", BigDecimal.ZERO,
                "ZEUR", new BigDecimal("34"), "XXRP", new BigDecimal("32"));
        final KrakenResponseDto<Map<String, BigDecimal>> krakenResponseDto = new KrakenResponseDto<>();
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

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal price = new BigDecimal("7000.3");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.<String>builder().build();
        final long orderExpirationInSecondsFromNow = 0;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());
        final CurrencyPairBo currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

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

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal price = new BigDecimal("7000.3");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.of("fciq");
        final long orderExpirationInSecondsFromNow = 0;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());
        final CurrencyPairBo currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

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

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal krakenPrice = new BigDecimal("7000.4");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.<String>builder().build();
        final long orderExpirationInSecondsFromNow = 0;
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, krakenPrice, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(new KrakenResponseDto<>());
        final CurrencyPairBo currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

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

        final String krakenMarketName = "XBTEUR";
        final String krakenOrderType = "buy";
        final String krakenPriceOrderType = "market";
        final BigDecimal price = new BigDecimal("7000.3");
        final BigDecimal volumeInQuoteCurrency = new BigDecimal("0.01");
        final ImmutableList<String> orderFlags = ImmutableList.<String>builder().build();
        final long orderExpirationInSecondsFromNow = 0;
        final KrakenResponseDto<KrakenAddOrderResultDto> krakenResponseDto = new KrakenResponseDto<>();
        krakenResponseDto.setError(List.of("Kraken error"));
        when(krakenPrivateApiConnector.addOrder(krakenMarketName, krakenOrderType,
                krakenPriceOrderType, price, volumeInQuoteCurrency, orderFlags,
                orderExpirationInSecondsFromNow)).thenReturn(krakenResponseDto);
        final CurrencyPairBo currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);

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
