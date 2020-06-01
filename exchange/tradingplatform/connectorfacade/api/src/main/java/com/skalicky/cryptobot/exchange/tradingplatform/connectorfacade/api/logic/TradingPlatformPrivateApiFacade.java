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

package com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TradingPlatformPrivateApiFacade extends TradingPlatformDesignated {

    @NotNull
    ImmutableList<OpenOrderBo> getOpenOrders(boolean includeTrades);

    @NotNull
    ImmutableList<ClosedOrderBo> getClosedOrders(boolean includeTrades,
                                                 @NotNull LocalDateTime from);

    @NotNull
    ImmutableMap<CurrencyBoEnum, BigDecimal> getAccountBalance();

    void placeOrder(@NotNull OrderTypeBoEnum orderType,
                    @NotNull PriceOrderTypeBoEnum priceOrderType,
                    @NotNull CurrencyPairBo currencyPair,
                    @NotNull BigDecimal volumeInQuoteCurrency,
                    @NotNull BigDecimal price,
                    boolean preferFeeInQuoteCurrency,
                    long orderExpirationInSecondsFromNow);
}
