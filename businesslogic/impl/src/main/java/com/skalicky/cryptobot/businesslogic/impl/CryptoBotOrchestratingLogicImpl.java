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
import com.skalicky.cryptobot.businesslogic.api.CryptoBotOrchestratingLogic;
import com.skalicky.cryptobot.businesslogic.impl.datetime.LocalDateTimeProvider;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CryptoBotOrchestratingLogicImpl implements CryptoBotOrchestratingLogic {

    @NotNull
    private final CryptoBotLogic cryptoBotLogic;
    @NotNull
    private final LocalDateTimeProvider localDateTimeProvider;

    public CryptoBotOrchestratingLogicImpl(@NotNull final CryptoBotLogic cryptoBotLogic,
                                           @NotNull final LocalDateTimeProvider localDateTimeProvider) {
        this.cryptoBotLogic = cryptoBotLogic;
        this.localDateTimeProvider = localDateTimeProvider;
    }

    @Override
    public void orchestrateExecution(@NotNull final String tradingPlatformName,
                                     @NotNull final BigDecimal volumeInBaseCurrencyToInvestPerRun,
                                     @NotNull final String baseCurrencyLabel,
                                     @NotNull final String quoteCurrencyLabel,
                                     @NotNull final BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal,
                                     final int minOffsetFromOpenDateTimeOfLastBuyOrderInHours) {

        final LocalDateTime from = localDateTimeProvider.now().minusDays(3);
        final ImmutableList<ClosedOrderBo> closedOrdersWithTrades = cryptoBotLogic.retrieveClosedOrdersWithTrades(from,
                tradingPlatformName);
        cryptoBotLogic.reportClosedOrders(closedOrdersWithTrades, from, tradingPlatformName);

        final ImmutableList<OpenOrderBo> openOrders = cryptoBotLogic.retrieveOpenOrders(tradingPlatformName);
        cryptoBotLogic.reportOpenOrders(openOrders, tradingPlatformName);

        cryptoBotLogic.placeBuyOrderIfEnoughAvailable(tradingPlatformName, volumeInBaseCurrencyToInvestPerRun,
                baseCurrencyLabel, quoteCurrencyLabel, offsetRatioOfLimitPriceToBidPriceInDecimal);
    }
}
