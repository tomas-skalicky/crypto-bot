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
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CryptoBotLogicImplUTest {

    @Nonnull
    private final TradingPlatformPrivateApiFacade privateApiFacade = createKrakenPrivateApiFacadeMock();
    @Nonnull
    private final SlackFacade slackFacade = mock(SlackFacade.class);
    @Nonnull
    private final CryptoBotLogicImpl cryptoBotLogicImpl = new CryptoBotLogicImpl(new ArrayList<>(),
            List.of(privateApiFacade), slackFacade);

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(privateApiFacade, slackFacade);
        Mockito.reset(privateApiFacade, slackFacade);
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_unsupportedTradingPlatform_then_exception() {
        assertThatThrownBy(() -> cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                "poloniex", BigDecimal.TEN, "EUR", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No private API facade for the trading platform \"poloniex\"");

        verify(privateApiFacade).getTradingPlatform();
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_tooLittleBaseCurrency_and_noSlackUrl_then_noPurchase_and_noSlackMessage() {
        when(privateApiFacade.getAccountBalance()).thenReturn(Map.of(CurrencyBoEnum.EUR, BigDecimal.ONE));

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                "kraken", BigDecimal.TEN, "EUR", null);

        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_tooLittleBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        when(privateApiFacade.getAccountBalance()).thenReturn(Map.of(CurrencyBoEnum.EUR, BigDecimal.ONE));
        final String slackUrl = "http://slack_url";

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                "kraken", BigDecimal.TEN, "EUR", slackUrl);

        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(slackFacade).sendMessage(
                "Too little base currency [1 EUR]. Needed volume to invest per run is 10 EUR", slackUrl);
    }

    @Test
    public void test_placeBuyOrderIfEnoughAvailable_when_enoughBaseCurrency_and_slackUrl_then_noPurchase_and_slackMessageSent() {
        when(privateApiFacade.getAccountBalance()).thenReturn(Map.of(CurrencyBoEnum.EUR, BigDecimal.TEN));
        final String slackUrl = "http://slack_url";

        cryptoBotLogicImpl.placeBuyOrderIfEnoughAvailable(
                "kraken", BigDecimal.TEN, "EUR", slackUrl);

        verify(privateApiFacade).getTradingPlatform();
        verify(privateApiFacade).getAccountBalance();
        verify(slackFacade).sendMessage(
                "Going to place a BUY order to buy for 10 EUR on kraken", slackUrl);
    }

    private TradingPlatformPrivateApiFacade createKrakenPrivateApiFacadeMock() {
        final TradingPlatformPrivateApiFacade facade = mock(TradingPlatformPrivateApiFacade.class);
        when(facade.getTradingPlatform()).thenReturn("kraken");
        return facade;
    }
}
