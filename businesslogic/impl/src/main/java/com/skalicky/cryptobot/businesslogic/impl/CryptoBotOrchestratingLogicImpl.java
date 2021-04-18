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
import com.skalicky.cryptobot.businesslogic.impl.constants.CryptoBotBusinessLogicConstants;
import com.skalicky.cryptobot.businesslogic.impl.datetime.LocalDateTimeProvider;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class CryptoBotOrchestratingLogicImpl implements CryptoBotOrchestratingLogic {

    @NotNull
    private static final Logger logger = LoggerFactory.getLogger(CryptoBotOrchestratingLogicImpl.class);

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

        final LocalDateTime orderLatestOpenDateTime = extractOrderLatestOpenDateTime(closedOrdersWithTrades,
                openOrders);
        final boolean minOffsetSatisfied = orderLatestOpenDateTime.plusHours(
                minOffsetFromOpenDateTimeOfLastBuyOrderInHours).compareTo(localDateTimeProvider.now()) <= 0;
        if (minOffsetSatisfied) {
            cryptoBotLogic.placeBuyOrderIfEnoughAvailable(tradingPlatformName, volumeInBaseCurrencyToInvestPerRun,
                    baseCurrencyLabel, quoteCurrencyLabel, offsetRatioOfLimitPriceToBidPriceInDecimal);
        } else {
            logger.info("Min required offset [{} hours] from open date-time of last BUY order [{}] is not satisfied.",
                    minOffsetFromOpenDateTimeOfLastBuyOrderInHours,
                    orderLatestOpenDateTime.format(CryptoBotBusinessLogicConstants.NOTIFICATION_DATE_TIME_FORMATTER));
        }
    }

    @NotNull
    private LocalDateTime extractOrderLatestOpenDateTime(final ImmutableList<ClosedOrderBo> closedOrdersWithTrades,
                                                         final ImmutableList<OpenOrderBo> openOrders) {
        final Optional<LocalDateTime> closedOrderLatestOpenDateTime = closedOrdersWithTrades.stream() //
                .map(ClosedOrderBo::getOpenDateTime) //
                .max(Comparator.naturalOrder());
        final Optional<LocalDateTime> openOrderLatestOpenDateTime = openOrders.stream() //
                .map(OpenOrderBo::getOpenDateTime) //
                .max(Comparator.naturalOrder());
        return Stream.of(closedOrderLatestOpenDateTime,
                openOrderLatestOpenDateTime) //
                .filter(Optional::isPresent) //
                .flatMap(dateTime -> Stream.of(dateTime.get())) //
                .max(Comparator.naturalOrder()) //
                .orElse(LocalDateTime.MIN);
    }
}
