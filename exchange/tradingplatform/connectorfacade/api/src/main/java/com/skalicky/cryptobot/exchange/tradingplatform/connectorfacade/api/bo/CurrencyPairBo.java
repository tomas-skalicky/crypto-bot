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

import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CurrencyPairBo {

    @NotNull
    private final CurrencyBoEnum quoteCurrency;
    @NotNull
    private final CurrencyBoEnum baseCurrency;

    public CurrencyPairBo(@NotNull final CurrencyBoEnum quoteCurrency,
                          @NotNull final CurrencyBoEnum baseCurrency) {
        this.quoteCurrency = quoteCurrency;
        this.baseCurrency = baseCurrency;
    }

    @NotNull
    public CurrencyBoEnum getQuoteCurrency() {
        return quoteCurrency;
    }

    @NotNull
    public CurrencyBoEnum getBaseCurrency() {
        return baseCurrency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quoteCurrency, baseCurrency);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final var that = (CurrencyPairBo) o;
        return quoteCurrency == that.quoteCurrency &&
                baseCurrency == that.baseCurrency;
    }
}
