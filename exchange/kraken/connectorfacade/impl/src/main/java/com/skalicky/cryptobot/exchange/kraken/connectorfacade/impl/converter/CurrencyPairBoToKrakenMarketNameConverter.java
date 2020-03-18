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
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

public class CurrencyPairBoToKrakenMarketNameConverter
        implements NonnullConverter<CurrencyPairBo, String> {

    @Override
    @Nonnull
    public String convert(@Nonnull final CurrencyPairBo currencyPair) {
        final var quoteCurrency = currencyPair.getQuoteCurrency();
        final var baseCurrency = currencyPair.getBaseCurrency();
        if (quoteCurrency == CurrencyBoEnum.BTC && baseCurrency == CurrencyBoEnum.EUR) {
            return "XBTEUR";
        } else {
            throw new IllegalArgumentException("Unsupported market [" + quoteCurrency.getLabel() + ", "
                    + baseCurrency.getLabel() + "]");
        }
    }
}
