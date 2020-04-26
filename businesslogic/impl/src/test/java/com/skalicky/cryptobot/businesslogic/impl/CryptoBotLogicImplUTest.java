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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CryptoBotLogicImplUTest {

    @Nonnull
    private final static String KRAKEN_TRADING_PLATFORM_NAME = "kraken";
    @Nonnull
    private final static String POLONIEX_TRADING_PLATFORM_NAME = "poloniex";
    @Nonnull
    private final TradingPlatformPublicApiFacade publicApiFacade = createKrakenPublicApiFacadeMock();
    @Nonnull
    private final TradingPlatformPrivateApiFacade privateApiFacade = createKrakenPrivateApiFacadeMock();
    @Nonnull
    private final SlackFacade slackFacade = mock(SlackFacade.class);
    @Nonnull
    private final FixableLocalDateTimeProvider fixableLocalDateTimeProvider = new FixableLocalDateTimeProviderImpl();
    @Nonnull
    private final CryptoBotLogicImpl cryptoBotLogicImpl = new CryptoBotLogicImpl(ImmutableList.of(publicApiFacade),
            ImmutableList.of(privateApiFacade), slackFacade, fixableLocalDateTimeProvider);

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(publicApiFacade, privateApiFacade, slackFacade);
        Mockito.reset(publicApiFacade, privateApiFacade, slackFacade);
    }

    @Test
    public void test_reportOpenOrders_when_unsupportedTradingPlatform_then_exception() {
        assertThatThrownBy(() -> cryptoBotLogicImpl.reportOpenOrders(
                POLONIEX_TRADING_PLATFORM_NAME, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"poloniex\"");

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
    }

    @Test
    public void test_reportOpenOrders_when_noSlackUrl_then_noSlackMessage() {
        final var includeTrades = true;
        when(privateApiFacade.getOpenOrders(includeTrades))
                .thenReturn(ImmutableList.of());

        cryptoBotLogicImpl.reportOpenOrders(KRAKEN_TRADING_PLATFORM_NAME, null);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getOpenOrders(includeTrades);
    }

    @Test
    public void test_reportOpenOrders_when_noOrder_and_providedSlackUrl_then_noneInSlackMessage() {
        final var includeTrades = true;
        when(privateApiFacade.getOpenOrders(includeTrades))
                .thenReturn(ImmutableList.of());
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.reportOpenOrders(KRAKEN_TRADING_PLATFORM_NAME, slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getOpenOrders(includeTrades);
        verify(slackFacade).sendMessage("Open orders on kraken: none", slackUrl);
    }

    @Test
    public void test_reportOpenOrders_when_twoOrders_then_twoInSlackMessage() {
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
        when(privateApiFacade.getOpenOrders(includeTrades))
                .thenReturn(ImmutableList.of(openOrder1, openOrder2));
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.reportOpenOrders(KRAKEN_TRADING_PLATFORM_NAME, slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getOpenOrders(includeTrades);
        verify(slackFacade).sendMessage("Open orders on kraken: \n" +
                "sell 0.65 BTC-EUR exec. 0 BTC @ market @ new -> no trades yet @ open 07.03. 08:15" +
                " expires on 09.03. 10:45 @ 0 trades\n" +
                "buy 150.56 EUR-BTC exec. 100 EUR @ limit 0.000176 exec. avg. 0.000175 fee 0.5 EUR @" +
                " partially executed -> trades exist @ open 06.03. 18:15 @ 2 trades", slackUrl);
    }

    @Test
    public void test_reportClosedOrders_when_unsupportedTradingPlatform_then_exception() {
        assertThatThrownBy(() -> cryptoBotLogicImpl.reportClosedOrders(
                POLONIEX_TRADING_PLATFORM_NAME, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"poloniex\"");

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
    }

    @Test
    public void test_reportClosedOrders_when_noSlackUrl_then_noSlackMessage() {
        final LocalDateTime now = fixableLocalDateTimeProvider.fix();
        final LocalDateTime fromDateTime = now.minusDays(3);
        final var includeTrades = true;
        when(privateApiFacade.getClosedOrders(includeTrades, fromDateTime))
                .thenReturn(ImmutableList.of());

        cryptoBotLogicImpl.reportClosedOrders(KRAKEN_TRADING_PLATFORM_NAME, null);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);
    }

    @Test
    public void test_reportClosedOrders_when_noOrder_and_providedSlackUrl_then_noneInSlackMessage() {
        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        when(privateApiFacade.getClosedOrders(includeTrades, fromDateTime))
                .thenReturn(ImmutableList.of());
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.reportClosedOrders(KRAKEN_TRADING_PLATFORM_NAME, slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);
        verify(slackFacade).sendMessage("Closed orders since 08.03. 10:30 on kraken: none", slackUrl);
    }

    @Test
    public void test_reportClosedOrders_when_twoOrders_then_twoInSlackMessage() {
        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        final ClosedOrderBo closedOrder1 = ClosedOrderBoBuilder.aClosedOrderBo()
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
        final ClosedOrderBo closedOrder2 = ClosedOrderBoBuilder.aClosedOrderBo()
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
        when(privateApiFacade.getClosedOrders(includeTrades, fromDateTime))
                .thenReturn(ImmutableList.of(closedOrder1, closedOrder2));
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.reportClosedOrders(KRAKEN_TRADING_PLATFORM_NAME, slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);
        verify(slackFacade).sendMessage("Closed orders since 08.03. 10:30 on kraken: \n" +
                "sell 0.65 BTC-EUR exec. 0.65 BTC @ market exec. avg. 5650 fee 0 BTC @" +
                " fully executed -> trades exist @ open 07.03. 08:15 close 07.03. 08:17 @ 1 trade\n" +
                "buy 150.56 EUR-BTC exec. 100 EUR @ limit 0.000176 exec. avg. 0.000175 fee 0.5 EUR @" +
                " partially executed -> trades exist @ open 06.03. 18:15 close 07.03. 08:05 @ 2 trades", slackUrl);
    }

    @Test
    public void test_reportClosedOrders_when_orderHasNoTrades_then_orderIsSkipped() {
        final var fromDateTime = LocalDateTime.of(2020, 3, 8, 10, 30);
        fixableLocalDateTimeProvider.fix(fromDateTime.plusDays(3));
        final var includeTrades = true;
        final ClosedOrderBo closedOrder = ClosedOrderBoBuilder.aClosedOrderBo()
                .withTradeIds(ImmutableList.of()).build();
        when(privateApiFacade.getClosedOrders(includeTrades, fromDateTime)).thenReturn(ImmutableList.of(closedOrder));
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.reportClosedOrders(KRAKEN_TRADING_PLATFORM_NAME, slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getClosedOrders(includeTrades, fromDateTime);
        verify(slackFacade).sendMessage("Closed orders since 08.03. 10:30 on kraken: none", slackUrl);
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_unsupportedTradingPlatform_then_exception() {
        assertThatThrownBy(() -> cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                POLONIEX_TRADING_PLATFORM_NAME, BigDecimal.TEN, "BTC", "XMR",
                new BigDecimal("0.01"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"poloniex\"");

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_tooLittleBaseCurrency_and_noSlackUrl_then_noPurchase_and_noSlackMessage() {
        when(privateApiFacade.getAccountBalance()).thenReturn(ImmutableMap.of(CurrencyBoEnum.EUR, BigDecimal.ONE));

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, BigDecimal.TEN, "EUR", "XRP",
                new BigDecimal("0.01"), null);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_tooLittleBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        when(privateApiFacade.getAccountBalance()).thenReturn(ImmutableMap.of(CurrencyBoEnum.EUR, BigDecimal.ONE));
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, BigDecimal.TEN, "EUR", "LTC",
                new BigDecimal("0.01"), slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(slackFacade).sendMessage(
                "Too little base currency [1 EUR]. Needed volume to invest per run is 10 EUR", slackUrl);
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_enoughBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        when(privateApiFacade.getAccountBalance()).thenReturn(ImmutableMap.of(CurrencyBoEnum.EUR, new BigDecimal(30)));
        final var ticker = new TickerBo("XXBTZEUR", BigDecimal.TEN, new BigDecimal(9));
        final var currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
        when(publicApiFacade.getTicker(currencyPair)).thenReturn(ticker);
        final var slackUrl = "http://slack_url";

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, new BigDecimal(20), "EUR",
                "BTC", new BigDecimal("0.001"), slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(publicApiFacade).getTicker(currencyPair);
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(privateApiFacade).placeOrder(OrderTypeBoEnum.BUY, PriceOrderTypeBoEnum.LIMIT, currencyPair,
                new BigDecimal("2.2244466689"), new BigDecimal("8.991"),
                true, 129_600);
        verify(slackFacade).sendMessage(
                "Going to retrieve a ticker for currencies quote BTC and base EUR on kraken.", slackUrl);
        verify(slackFacade).sendMessage(
                "limit order to buy 2.2244466689 BTC for 20 EUR successfully placed on kraken." +
                        " Limit price of 1 BTC = 8.991 EUR." +
                        " Order expiration is in 129600 seconds from now.",
                slackUrl);
    }

    private TradingPlatformPublicApiFacade createKrakenPublicApiFacadeMock() {
        final var facade = mock(TradingPlatformPublicApiFacade.class);
        when(facade.getTradingPlatform()).thenReturn(KRAKEN_TRADING_PLATFORM_NAME);
        return facade;
    }

    private TradingPlatformPrivateApiFacade createKrakenPrivateApiFacadeMock() {
        final var facade = mock(TradingPlatformPrivateApiFacade.class);
        when(facade.getTradingPlatform()).thenReturn(KRAKEN_TRADING_PLATFORM_NAME);
        return facade;
    }
}
