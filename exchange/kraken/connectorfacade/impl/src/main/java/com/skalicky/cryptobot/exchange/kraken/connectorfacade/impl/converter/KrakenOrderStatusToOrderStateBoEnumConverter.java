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

import javax.annotation.Nonnull;

// TODO Tomas 3 consider trades in the converter
public class KrakenOrderStatusToOrderStateBoEnumConverter implements NonnullConverter<String, OrderStateBoEnum> {

    @Override
    @Nonnull
    public OrderStateBoEnum convert(@Nonnull final String priceOrderType) {
        switch (priceOrderType) {
            case "open":
                return OrderStateBoEnum.NEW;
            case "closed":
                return OrderStateBoEnum.FULLY_EXECUTED;
            case "canceled":
                return OrderStateBoEnum.FULLY_CANCELED;
            default:
                throw new IllegalArgumentException("Unsupported order type [" + priceOrderType + "]");
        }
    }
}
