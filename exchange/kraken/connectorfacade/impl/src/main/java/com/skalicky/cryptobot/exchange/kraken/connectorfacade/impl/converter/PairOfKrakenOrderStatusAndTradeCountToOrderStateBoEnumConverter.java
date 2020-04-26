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

package com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter;

import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter implements NonnullConverter<Pair<String, Integer>, OrderStateBoEnum> {

    @Override
    @Nonnull
    public OrderStateBoEnum convert(@Nonnull final Pair<String, Integer> krakenOrderStatusAndTradeCount) {
        final String krakenOrderStatus = krakenOrderStatusAndTradeCount.getLeft();
        final Integer tradeCount = krakenOrderStatusAndTradeCount.getRight();
        switch (krakenOrderStatus) {
            case "open":
                return (tradeCount == 0) ? OrderStateBoEnum.NEW : OrderStateBoEnum.PARTIALLY_EXECUTED;
            case "closed":
                return OrderStateBoEnum.FULLY_EXECUTED;
            case "canceled":
                return (tradeCount == 0) ? OrderStateBoEnum.FULLY_CANCELED : OrderStateBoEnum.PARTIALLY_EXECUTED_THEN_CANCELED;
            case "expired":
                return (tradeCount == 0) ? OrderStateBoEnum.FULLY_EXPIRED : OrderStateBoEnum.PARTIALLY_EXECUTED_THEN_EXPIRED;
            default:
                throw new IllegalArgumentException("Unsupported Kraken order status [" + krakenOrderStatus + "]");
        }
    }
}
