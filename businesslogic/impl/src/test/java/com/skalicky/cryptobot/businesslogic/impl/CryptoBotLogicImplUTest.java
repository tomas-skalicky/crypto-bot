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

import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.SlackFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPublicApiFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CryptoBotLogicImplUTest {

    @Nonnull
    private final static String KRAKEN_TRADING_PLATFORM_NAME = "kraken";
    @Nonnull
    private final TradingPlatformPublicApiFacade publicApiFacade = createKrakenPublicApiFacadeMock();
    @Nonnull
    private final TradingPlatformPrivateApiFacade privateApiFacade = createKrakenPrivateApiFacadeMock();
    @Nonnull
    private final SlackFacade slackFacade = mock(SlackFacade.class);
    @Nonnull
    private final CryptoBotLogicImpl cryptoBotLogicImpl = new CryptoBotLogicImpl(List.of(publicApiFacade),
            List.of(privateApiFacade), slackFacade);

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(publicApiFacade, privateApiFacade, slackFacade);
        Mockito.reset(publicApiFacade, privateApiFacade, slackFacade);
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_unsupportedTradingPlatform_then_exception() {
        assertThatThrownBy(() -> cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                "poloniex", BigDecimal.TEN, "BTC", "XMR",
                null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"poloniex\"");

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_tooLittleBaseCurrency_and_noSlackUrl_then_noPurchase_and_noSlackMessage() {
        when(privateApiFacade.getAccountBalance()).thenReturn(Map.of(CurrencyBoEnum.EUR, BigDecimal.ONE));

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, BigDecimal.TEN, "EUR", "XRP",
                null);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_tooLittleBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        when(privateApiFacade.getAccountBalance()).thenReturn(Map.of(CurrencyBoEnum.EUR, BigDecimal.ONE));
        final String slackUrl = "http://slack_url";

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, BigDecimal.TEN, "EUR", "LTC",
                slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(slackFacade).sendMessage(
                "Too little base currency [1 EUR]. Needed volume to invest per run is 10 EUR", slackUrl);
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_enoughBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        when(privateApiFacade.getAccountBalance()).thenReturn(Map.of(CurrencyBoEnum.EUR, new BigDecimal(30)));
        final TickerBo ticker = new TickerBo("XXBTZEUR", BigDecimal.TEN, new BigDecimal(9));
        when(publicApiFacade.getTicker(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR)).thenReturn(ticker);
        final String slackUrl = "http://slack_url";

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                KRAKEN_TRADING_PLATFORM_NAME, new BigDecimal(20), "EUR",
                "BTC", slackUrl);

        verify(publicApiFacade).getTradingPlatform();
        verify(publicApiFacade).getTicker(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(privateApiFacade).placeOrder(OrderTypeBoEnum.BUY, PriceOrderTypeBoEnum.LIMIT, CurrencyBoEnum.EUR,
                CurrencyBoEnum.BTC, new BigDecimal("2.2446689113"), new BigDecimal("8.91"),
                true, 129_600);
        verify(slackFacade).sendMessage(
                "Going to retrieve a ticker for currencies quote BTC and base EUR on kraken.", slackUrl);
        verify(slackFacade).sendMessage(
                "limit order to buy 2.2446689113 BTC for 20 EUR successfully placed on kraken." +
                        " Limit price of 1 BTC = 8.91 EUR." +
                        " Order expiration is in 129600 seconds from now.",
                slackUrl);
    }

    private TradingPlatformPublicApiFacade createKrakenPublicApiFacadeMock() {
        final TradingPlatformPublicApiFacade facade = mock(TradingPlatformPublicApiFacade.class);
        when(facade.getTradingPlatform()).thenReturn(KRAKEN_TRADING_PLATFORM_NAME);
        return facade;
    }

    private TradingPlatformPrivateApiFacade createKrakenPrivateApiFacadeMock() {
        final TradingPlatformPrivateApiFacade facade = mock(TradingPlatformPrivateApiFacade.class);
        when(facade.getTradingPlatform()).thenReturn(KRAKEN_TRADING_PLATFORM_NAME);
        return facade;
    }
}
