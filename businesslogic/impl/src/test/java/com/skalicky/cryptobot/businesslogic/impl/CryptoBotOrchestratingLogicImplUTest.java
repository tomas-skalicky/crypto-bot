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
import com.skalicky.cryptobot.businesslogic.impl.datetime.FixableLocalDateTimeProvider;
import com.skalicky.cryptobot.businesslogic.impl.datetime.FixableLocalDateTimeProviderImpl;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBoBuilder;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBoBuilder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.BDDMockito.willDoNothing;

public class CryptoBotOrchestratingLogicImplUTest {

    @NotNull
    private final CryptoBotLogic cryptoBotLogic = mock(CryptoBotLogic.class);
    @NotNull
    private final FixableLocalDateTimeProvider fixableLocalDateTimeProvider = new FixableLocalDateTimeProviderImpl();
    @NotNull
    private final CryptoBotOrchestratingLogicImpl cryptoBotOrchestratingLogicImpl = new CryptoBotOrchestratingLogicImpl(
            cryptoBotLogic, fixableLocalDateTimeProvider);

    @AfterEach
    public void assertAndCleanMocks() {
        verifyNoMoreInteractions(cryptoBotLogic);
        reset(cryptoBotLogic);
    }

    @Test
    public void test_orchestrateExecution_when_retrieveClosedOrdersWithTradesThrowsException_then_exceptionIsNotCaught() {
        // Given
        fixableLocalDateTimeProvider.fix();

        final String tradingPlatformName = "bittrex";
        given(cryptoBotLogic.retrieveClosedOrdersWithTrades(any(LocalDateTime.class),
                eq(tradingPlatformName))).willThrow(IllegalArgumentException.class);
        final BigDecimal volumeInBaseCurrencyToInvestPerRun = new BigDecimal("155");
        final String baseCurrencyLabel = "USDT";
        final String quoteCurrencyLabel = "MNR";
        final BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal = new BigDecimal("0.025");
        final int minOffsetFromOpenDateTimeOfLastBuyOrderInHours = 36;

        // When
        final Throwable caughtThrowable = catchThrowable(() -> cryptoBotOrchestratingLogicImpl.orchestrateExecution(
                tradingPlatformName, volumeInBaseCurrencyToInvestPerRun, baseCurrencyLabel, quoteCurrencyLabel,
                offsetRatioOfLimitPriceToBidPriceInDecimal, minOffsetFromOpenDateTimeOfLastBuyOrderInHours));

        // Then
        final ArgumentCaptor<LocalDateTime> fromInRetrieve = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(cryptoBotLogic).retrieveClosedOrdersWithTrades(fromInRetrieve.capture(), eq(tradingPlatformName));

        then(caughtThrowable).isInstanceOf(IllegalArgumentException.class);
        then(fromInRetrieve.getValue()).isEqualTo(fixableLocalDateTimeProvider.now().minusDays(3));
    }

    @Test
    public void test_orchestrateExecution_when_validInputData_then_noException() {
        // Given
        fixableLocalDateTimeProvider.fix();

        final String tradingPlatformName = "bittrex";
        final ImmutableList<ClosedOrderBo> closedOrdersWithTrades = ImmutableList.of(
                ClosedOrderBoBuilder.aClosedOrderBo().withOrderId("dummy_closed_order_id").build());
        given(cryptoBotLogic.retrieveClosedOrdersWithTrades(any(LocalDateTime.class),
                eq(tradingPlatformName))).willReturn(closedOrdersWithTrades);

        willDoNothing().given(cryptoBotLogic).reportClosedOrders(eq(closedOrdersWithTrades), any(LocalDateTime.class),
                eq(tradingPlatformName));

        final ImmutableList<OpenOrderBo> openOrders = ImmutableList.of(
                OpenOrderBoBuilder.aOpenOrderBo().withOrderId("dummy_open_order_id").build());
        given(cryptoBotLogic.retrieveOpenOrders(tradingPlatformName)).willReturn(openOrders);

        willDoNothing().given(cryptoBotLogic).reportOpenOrders(openOrders, tradingPlatformName);

        final BigDecimal volumeInBaseCurrencyToInvestPerRun = new BigDecimal("155");
        final String baseCurrencyLabel = "USDT";
        final String quoteCurrencyLabel = "MNR";
        final BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal = new BigDecimal("0.025");
        final int minOffsetFromOpenDateTimeOfLastBuyOrderInHours = 36;

        // When
        cryptoBotOrchestratingLogicImpl.orchestrateExecution(tradingPlatformName, volumeInBaseCurrencyToInvestPerRun,
                baseCurrencyLabel, quoteCurrencyLabel, offsetRatioOfLimitPriceToBidPriceInDecimal,
                minOffsetFromOpenDateTimeOfLastBuyOrderInHours);

        // Then
        final ArgumentCaptor<LocalDateTime> fromInRetrieve = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(cryptoBotLogic).retrieveClosedOrdersWithTrades(fromInRetrieve.capture(), eq(tradingPlatformName));
        final ArgumentCaptor<LocalDateTime> fromInReport = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(cryptoBotLogic).reportClosedOrders(eq(closedOrdersWithTrades), fromInReport.capture(),
                eq(tradingPlatformName));
        verify(cryptoBotLogic).retrieveOpenOrders(tradingPlatformName);
        verify(cryptoBotLogic).reportOpenOrders(openOrders, tradingPlatformName);
        verify(cryptoBotLogic).placeBuyOrderIfEnoughAvailable(tradingPlatformName, volumeInBaseCurrencyToInvestPerRun,
                baseCurrencyLabel, quoteCurrencyLabel, offsetRatioOfLimitPriceToBidPriceInDecimal);

        final LocalDateTime expectedFrom = fixableLocalDateTimeProvider.now().minusDays(3);
        then(fromInRetrieve.getValue()).isEqualTo(expectedFrom);
        then(fromInReport.getValue()).isEqualTo(expectedFrom);
    }
}
