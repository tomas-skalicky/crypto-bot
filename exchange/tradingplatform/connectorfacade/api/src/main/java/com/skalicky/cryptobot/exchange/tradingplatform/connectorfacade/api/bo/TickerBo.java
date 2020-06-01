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

package com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public final class TickerBo {
    @NotNull
    private final String tickerName;
    @NotNull
    private final BigDecimal askPrice;
    @NotNull
    private final BigDecimal bidPrice;

    public TickerBo(@NotNull final String tickerName,
                    @NotNull final BigDecimal askPrice,
                    @NotNull final BigDecimal bidPrice) {
        this.tickerName = tickerName;
        this.askPrice = askPrice;
        this.bidPrice = bidPrice;
    }

    @NotNull
    public String getTickerName() {
        return tickerName;
    }

    @NotNull
    public BigDecimal getAskPrice() {
        return askPrice;
    }

    @NotNull
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    @Override
    public String toString() {
        return "TickerBo{" +
                "marketName='" + tickerName + '\'' +
                ", askPrice=" + askPrice +
                ", bidPrice=" + bidPrice +
                '}';
    }
}
