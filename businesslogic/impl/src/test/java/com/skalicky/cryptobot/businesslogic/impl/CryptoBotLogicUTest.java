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

package com.skalicky.cryptobot.businesslogic.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.businesslogic.api.model.VolumeMultiplierBo;
import com.skalicky.cryptobot.businesslogic.impl.datetime.FixableLocalDateTimeProvider;
import com.skalicky.cryptobot.businesslogic.impl.datetime.FixableLocalDateTimeProviderImpl;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.logic.SlackFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBoBuilder;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBoBuilder;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPublicApiFacade;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

public class CryptoBotLogicUTest {

    @NotNull
    private final static String KRAKEN_TRADING_PLATFORM_NAME = "kraken";
    @NotNull
    private final TradingPlatformPublicApiFacade publicApiFacade = createPublicApiFacadeMock(
            KRAKEN_TRADING_PLATFORM_NAME);
    @NotNull
    private final TradingPlatformPrivateApiFacade privateApiFacade = createPrivateApiFacadeMock(
            KRAKEN_TRADING_PLATFORM_NAME);
    @NotNull
    private final SlackFacade slackFacade = mock(SlackFacade.class);
    @NotNull
    private final FixableLocalDateTimeProvider fixableLocalDateTimeProvider = new FixableLocalDateTimeProviderImpl();
    @NotNull
    private final CryptoBotLogic cryptoBotLogic = new CryptoBotLogic(ImmutableList.of(publicApiFacade),
            ImmutableList.of(privateApiFacade), slackFacade);

    @AfterEach
    public void assertAndCleanMocks() {
        verifyNoMoreInteractions(publicApiFacade, privateApiFacade, slackFacade);
        reset(publicApiFacade, privateApiFacade, slackFacade);
    }

    @Test
    public void test_retrieveOpenOrders_when_unsupportedTradingPlatform_then_exception() {
        // When
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final Throwable caughtThrowable = catchThrowable(() -> cryptoBotLogic.retrieveOpenOrders(
                "bittrex"));

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        then(caughtThrowable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"bittrex\"");
    }

    @Test
    public void test_retrieveOpenOrders_when_noOrder_then_emptyResultList() {
        // Given
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final var includeTrades = true;
        given(privateApiFacade.getOpenOrders(includeTrades))
                .willReturn(ImmutableList.of());

        // When
        final ImmutableList<OpenOrderBo> actualOpenOrders = cryptoBotLogic.retrieveOpenOrders(tradingPlatformName);

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getOpenOrders(includeTrades);

        then(actualOpenOrders).isEmpty();
    }

    @Test
    public void test_retrieveOpenOrders_when_twoOrders_then_resultListWithTwoItems() {
        // Given
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final var includeTrades = true;
        given(privateApiFacade.getOpenOrders(includeTrades))
                .willReturn(ImmutableList.of(OpenOrderBoBuilder.aOpenOrderBo().build(),
                        OpenOrderBoBuilder.aOpenOrderBo().build()));

        // When
        final ImmutableList<OpenOrderBo> actualOpenOrders = cryptoBotLogic.retrieveOpenOrders(tradingPlatformName);

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getOpenOrders(includeTrades);

        then(actualOpenOrders).hasSize(2);
    }

    @Test
    public void test_reportOpenOrders_when_noOrder_then_appropriateMessageIsToBeSentViaSlack() {
        // Given
        final var includeTrades = true;
        final ImmutableList<OpenOrderBo> openOrders = ImmutableList.of();
        given(privateApiFacade.getOpenOrders(includeTrades))
                .willReturn(openOrders);

        // When
        cryptoBotLogic.reportOpenOrders(openOrders, "poloniex");

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        verify(slackFacade).sendMessage("Open orders on poloniex: none");
    }

    @Test
    public void test_reportOpenOrders_when_twoOrders_then_twoInSlackMessage() {
        // Given
        final var includeTrades = true;
        final OpenOrderBo openOrder1 = OpenOrderBoBuilder.aOpenOrderBo()
                .withOrderType(OrderTypeBoEnum.SELL)
                .withDesiredVolumeInQuoteCurrency(new BigDecimal("0.65"))
                .withCurrencyPair(new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR))
                .withAlreadyExecutedVolumeInQuoteCurrency(BigDecimal.ZERO)
                .withPriceOrderType(PriceOrderTypeBoEnum.MARKET)
                .withDesiredPrice(null)
                .withAverageActualPrice(null)
                .withActualFeeInQuoteCurrency(BigDecimal.ZERO)
                .withStatus(OrderStateBoEnum.NEW)
                .withOpenDateTime(LocalDateTime.of(2020, 3, 7, 8, 15))
                .withExpirationDateTime(LocalDateTime.of(2020, 3, 9, 10, 45))
                .withTradeIds(ImmutableList.of()).build();
        final OpenOrderBo openOrder2 = OpenOrderBoBuilder.aOpenOrderBo()
                .withOrderType(OrderTypeBoEnum.BUY)
                .withDesiredVolumeInQuoteCurrency(new BigDecimal("150.56"))
                .withCurrencyPair(new CurrencyPairBo(CurrencyBoEnum.EUR, CurrencyBoEnum.BTC))
                .withAlreadyExecutedVolumeInQuoteCurrency(BigDecimal.valueOf(100))
                .withPriceOrderType(PriceOrderTypeBoEnum.LIMIT)
                .withDesiredPrice(new BigDecimal("0.000176"))
                .withAverageActualPrice(new BigDecimal("0.000175"))
                .withActualFeeInQuoteCurrency(new BigDecimal("0.5"))
                .withStatus(OrderStateBoEnum.PARTIALLY_EXECUTED)
                .withOpenDateTime(LocalDateTime.of(2020, 3, 6, 18, 15))
                .withTradeIds(ImmutableList.of("tradeId2", "tradeId3")).build();
        final ImmutableList<OpenOrderBo> openOrders = ImmutableList.of(openOrder1, openOrder2);
        given(privateApiFacade.getOpenOrders(includeTrades))
                .willReturn(openOrders);

        // When
        cryptoBotLogic.reportOpenOrders(openOrders, "coinbase");

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        verify(slackFacade).sendMessage("Open orders on coinbase: \n" +
                "sell 0.65 BTC-EUR exec. 0 BTC @ market @ new -> no trades yet @ open 07.03. 08:15" +
                " expires on 09.03. 10:45 @ 0 trades\n" +
                "buy 150.56 EUR-BTC exec. 100 EUR @ limit 0.000176 exec. avg. 0.000175 fee 0.5 EUR @" +
                " partially executed -> trades exist @ open 06.03. 18:15 @ 2 trades");
    }

    @Test
    public void test_retrieveClosedOrdersWithTrades_when_unsupportedTradingPlatform_then_exception() {
        // When
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final LocalDateTime from = fixableLocalDateTimeProvider.fix().minusHours(2);

        final Throwable caughtThrowable = catchThrowable(() -> cryptoBotLogic.retrieveClosedOrdersWithTrades(from,
                "bittrex"));

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        then(caughtThrowable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"bittrex\"");
    }

    @Test
    public void test_retrieveClosedOrdersWithTrades_when_noOrder_then_emptyResultList() {
        // Given
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final var fromDateTime = LocalDateTime.of(2021, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        given(privateApiFacade.getClosedOrders(includeTrades, fromDateTime))
                .willReturn(ImmutableList.of());

        // When
        final ImmutableList<ClosedOrderBo> actualClosedOrdersWithTrades = cryptoBotLogic.retrieveClosedOrdersWithTrades(
                fromDateTime, tradingPlatformName);

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);

        then(actualClosedOrdersWithTrades).isEmpty();
    }

    @Test
    public void test_retrieveClosedOrdersWithTrades_when_twoOrdersWithTrades_then_resultListWithTwoItems() {
        // Given
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        final ClosedOrderBo closedOrder1WithTrades = ClosedOrderBoBuilder.aClosedOrderBo()
                .withTradeIds(ImmutableList.of("tradeId1")).build();
        final ClosedOrderBo closedOrder2WithTrades = ClosedOrderBoBuilder.aClosedOrderBo()
                .withTradeIds(ImmutableList.of("tradeId2", "tradeId3")).build();
        given(privateApiFacade.getClosedOrders(includeTrades, fromDateTime))
                .willReturn(ImmutableList.of(closedOrder1WithTrades, closedOrder2WithTrades));

        // When
        final ImmutableList<ClosedOrderBo> actualClosedOrdersWithTrades = cryptoBotLogic.retrieveClosedOrdersWithTrades(
                fromDateTime, tradingPlatformName);

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);

        then(actualClosedOrdersWithTrades).hasSize(2);
    }

    @Test
    public void test_retrieveClosedOrdersWithTrades_when_orderHasNoTrades_then_orderIsSkipped() {
        // Given
        final String tradingPlatformName = KRAKEN_TRADING_PLATFORM_NAME;
        given(publicApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);
        given(privateApiFacade.getTradingPlatform()).willReturn(tradingPlatformName);

        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        final ClosedOrderBo closedOrderWithNoTrades = ClosedOrderBoBuilder.aClosedOrderBo()
                .withTradeIds(ImmutableList.of()).build();
        given(privateApiFacade.getClosedOrders(includeTrades, fromDateTime)).willReturn(
                ImmutableList.of(closedOrderWithNoTrades));

        // When
        final ImmutableList<ClosedOrderBo> actualClosedOrdersWithTrades = cryptoBotLogic.retrieveClosedOrdersWithTrades(
                fromDateTime, tradingPlatformName);

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);

        then(actualClosedOrdersWithTrades).isEmpty();
    }

    @Test
    public void test_reportClosedOrders_when_noOrder_then_appropriateMessageIsToBeSentViaSlack() {
        // Given
        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        final ImmutableList<ClosedOrderBo> closedOrdersWithTrades = ImmutableList.of();
        given(privateApiFacade.getClosedOrders(includeTrades, fromDateTime))
                .willReturn(closedOrdersWithTrades);

        // When
        cryptoBotLogic.reportClosedOrders(closedOrdersWithTrades, fromDateTime, "CEX.IO");

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        verify(slackFacade).sendMessage("Closed orders since 08.03. 10:30 on CEX.IO: none");
    }

    @Test
    public void test_reportClosedOrders_when_twoOrders_then_twoInSlackMessage() {
        // Given
        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        final ClosedOrderBo closedOrder1WithTrades = ClosedOrderBoBuilder.aClosedOrderBo()
                .withOrderType(OrderTypeBoEnum.SELL)
                .withDesiredVolumeInQuoteCurrency(new BigDecimal("0.65"))
                .withCurrencyPair(new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR))
                .withTotalExecutedVolumeInQuoteCurrency(new BigDecimal("0.65"))
                .withPriceOrderType(PriceOrderTypeBoEnum.MARKET)
                .withDesiredPrice(null)
                .withAverageActualPrice(BigDecimal.valueOf(5650))
                .withActualFeeInQuoteCurrency(BigDecimal.ZERO)
                .withStatus(OrderStateBoEnum.FULLY_EXECUTED)
                .withOpenDateTime(LocalDateTime.of(2020, 3, 7, 8, 15))
                .withCloseDateTime(LocalDateTime.of(2020, 3, 7, 8, 17))
                .withTradeIds(ImmutableList.of("tradeId1")).build();
        final ClosedOrderBo closedOrder2WithTrades = ClosedOrderBoBuilder.aClosedOrderBo()
                .withOrderType(OrderTypeBoEnum.BUY)
                .withDesiredVolumeInQuoteCurrency(new BigDecimal("150.56"))
                .withCurrencyPair(new CurrencyPairBo(CurrencyBoEnum.EUR, CurrencyBoEnum.BTC))
                .withTotalExecutedVolumeInQuoteCurrency(BigDecimal.valueOf(100))
                .withPriceOrderType(PriceOrderTypeBoEnum.LIMIT)
                .withDesiredPrice(new BigDecimal("0.000176"))
                .withAverageActualPrice(new BigDecimal("0.000175"))
                .withActualFeeInQuoteCurrency(new BigDecimal("0.5"))
                .withStatus(OrderStateBoEnum.PARTIALLY_EXECUTED)
                .withOpenDateTime(LocalDateTime.of(2020, 3, 6, 18, 15))
                .withCloseDateTime(LocalDateTime.of(2020, 3, 7, 8, 5))
                .withTradeIds(ImmutableList.of("tradeId2", "tradeId3")).build();
        final ImmutableList<ClosedOrderBo> closedOrdersWithTrades = ImmutableList.of(closedOrder1WithTrades,
                closedOrder2WithTrades);
        given(privateApiFacade.getClosedOrders(includeTrades, fromDateTime)).willReturn(closedOrdersWithTrades);

        // When
        cryptoBotLogic.reportClosedOrders(closedOrdersWithTrades, fromDateTime, "kraken");

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        verify(slackFacade).sendMessage("Closed orders since 08.03. 10:30 on kraken: \n" +
                "sell 0.65 BTC-EUR exec. 0.65 BTC @ market exec. avg. 5650 fee 0 BTC @" +
                " fully executed -> trades exist @ open 07.03. 08:15 close 07.03. 08:17 @ 1 trade\n" +
                "buy 150.56 EUR-BTC exec. 100 EUR @ limit 0.000176 exec. avg. 0.000175 fee 0.5 EUR @" +
                " partially executed -> trades exist @ open 06.03. 18:15 close 07.03. 08:05 @ 2 trades");
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_unsupportedTradingPlatform_then_exception() {
        // Given
        final ImmutableList<VolumeMultiplierBo> volumeMultiplierOrderedAscByPrice = ImmutableList.of(
                new VolumeMultiplierBo(null, BigDecimal.ONE));

        // When
        final Throwable caughtThrowable = catchThrowable(() -> cryptoBotLogic.placeBuyOrderIfEnoughAvailable(
                "bittrex", BigDecimal.TEN, volumeMultiplierOrderedAscByPrice, "BTC", "XMR", new BigDecimal("0.01")));

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();

        then(caughtThrowable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"bittrex\"");
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_volumeMultiplier2_and_tooLittleBaseCurrency_then_noPurchase_and_appropriateMessageIsToBeSentViaSlack() {
        // Given
        given(privateApiFacade.getAccountBalance()).willReturn(ImmutableMap.of(CurrencyBoEnum.EUR, new BigDecimal(11)));
        final var ticker = new TickerBo("XXBTZEUR", new BigDecimal(25100), new BigDecimal(25000));
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
        given(publicApiFacade.getTicker(currencyPair)).willReturn(ticker);

        final ImmutableList<VolumeMultiplierBo> volumeMultiplierOrderedAscByPrice = ImmutableList.of(
                new VolumeMultiplierBo(null, new BigDecimal(2)));

        // When
        cryptoBotLogic.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, BigDecimal.TEN, volumeMultiplierOrderedAscByPrice, "EUR", "BTC",
                new BigDecimal("0.01"));

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(publicApiFacade).getTicker(currencyPair);
        verify(slackFacade).sendMessage(
                "Going to retrieve a ticker for currencies quote BTC and base EUR on kraken.");
        verify(slackFacade).sendMessage(
                "Too little base currency [11 EUR]. Needed volume to invest per run is 20 EUR");
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_enoughBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        // Given
        given(privateApiFacade.getAccountBalance()).willReturn(ImmutableMap.of(CurrencyBoEnum.EUR, new BigDecimal(30)));
        final var ticker = new TickerBo("XXBTZEUR", BigDecimal.TEN, new BigDecimal(9));
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
        given(publicApiFacade.getTicker(currencyPair)).willReturn(ticker);

        final ImmutableList<VolumeMultiplierBo> volumeMultiplierOrderedAscByPrice = ImmutableList.of(
                new VolumeMultiplierBo(null, BigDecimal.ONE));

        // When
        cryptoBotLogic.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, new BigDecimal(20), volumeMultiplierOrderedAscByPrice, "EUR", "BTC",
                new BigDecimal("0.001"));

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(publicApiFacade).getTicker(currencyPair);
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(privateApiFacade).placeOrder(OrderTypeBoEnum.BUY, PriceOrderTypeBoEnum.LIMIT, currencyPair,
                new BigDecimal("2.2244466689"), new BigDecimal("8.991"),
                true, 129_600);
        verify(slackFacade).sendMessage(
                "Going to retrieve a ticker for currencies quote BTC and base EUR on kraken.");
        verify(slackFacade).sendMessage(
                "limit order to buy 2.2244466689 BTC for 20 EUR successfully placed on kraken." +
                        " Limit price of 1 BTC = 8.991 EUR." +
                        " Order expiration is in 129600 seconds from now.");
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_volumeMultiplierDifferentFrom1_then_volumeToPurchaseDifferentFromInput() {
        // Given
        given(privateApiFacade.getAccountBalance()).willReturn(ImmutableMap.of(CurrencyBoEnum.EUR, new BigDecimal(30)));
        final var ticker = new TickerBo("XXBTZEUR", new BigDecimal(25100), new BigDecimal(25000));
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
        given(publicApiFacade.getTicker(currencyPair)).willReturn(ticker);

        final ImmutableList<VolumeMultiplierBo> volumeMultiplierOrderedAscByPrice = ImmutableList.of(
                new VolumeMultiplierBo(new BigDecimal("25000.000001"), new BigDecimal("1.5")),
                new VolumeMultiplierBo(null, BigDecimal.ONE));

        // When
        cryptoBotLogic.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, new BigDecimal(20), volumeMultiplierOrderedAscByPrice, "EUR", "BTC",
                BigDecimal.ZERO);

        // Then
        verify(publicApiFacade).getTradingPlatform();
        verify(publicApiFacade).getTicker(currencyPair);
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(privateApiFacade).placeOrder(OrderTypeBoEnum.BUY, PriceOrderTypeBoEnum.LIMIT, currencyPair,
                new BigDecimal("0.0012000000"), new BigDecimal(25000),
                true, 129_600);
        verify(slackFacade).sendMessage(
                "Going to retrieve a ticker for currencies quote BTC and base EUR on kraken.");
        verify(slackFacade).sendMessage(
                "limit order to buy 0.0012000000 BTC for 30.0 EUR successfully placed on kraken." +
                        " Limit price of 1 BTC = 25000 EUR." +
                        " Order expiration is in 129600 seconds from now.");
    }

    private TradingPlatformPublicApiFacade createPublicApiFacadeMock(@NotNull final String tradingPlatformName) {
        final var facade = mock(TradingPlatformPublicApiFacade.class);
        given(facade.getTradingPlatform()).willReturn(tradingPlatformName);
        return facade;
    }

    private TradingPlatformPrivateApiFacade createPrivateApiFacadeMock(@NotNull final String tradingPlatformName) {
        final var facade = mock(TradingPlatformPrivateApiFacade.class);
        given(facade.getTradingPlatform()).willReturn(tradingPlatformName);
        return facade;
    }
}
