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

import javax.annotation.Nonnull;
import java.math.BigDecimal;

public class TickerBo {
    @Nonnull
    private final String marketName;
    @Nonnull
    private final BigDecimal askPrice;
    @Nonnull
    private final BigDecimal bidPrice;

    public TickerBo(@Nonnull final String marketName,
                    @Nonnull final BigDecimal askPrice,
                    @Nonnull final BigDecimal bidPrice) {
        this.marketName = marketName;
        this.askPrice = askPrice;
        this.bidPrice = bidPrice;
    }

    @Nonnull
    public String getMarketName() {
        return marketName;
    }

    @Nonnull
    public BigDecimal getAskPrice() {
        return askPrice;
    }

    @Nonnull
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    @Override
    public String toString() {
        return "TickerBo{" +
                "marketName='" + marketName + '\'' +
                ", askPrice=" + askPrice +
                ", bidPrice=" + bidPrice +
                '}';
    }
}
