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

package com.skalicky.cryptobot.exchange.kraken.facade.impl.converter;

import com.skalicky.cryptobot.exchange.shared.facade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.shared.facade.api.converter.NonnullConverter;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Input data structure:
 *
 * <pre>
 *     <pair_name> = pair name
 *     a = ask array(<price>, <whole lot volume>, <lot volume>),
 *     b = bid array(<price>, <whole lot volume>, <lot volume>),
 *     c = last trade closed array(<price>, <lot volume>),
 *     v = volume array(<today>, <last 24 hours>),
 *     p = volume weighted average price array(<today>, <last 24 hours>),
 *     t = number of trades array(<today>, <last 24 hours>),
 *     l = low array(<today>, <last 24 hours>),
 *     h = high array(<today>, <last 24 hours>),
 *     o = today's opening price
 * </pre>
 */
public class KrakenMapEntryToTickerBoConverter implements NonnullConverter<Map.Entry<String, Map<String, Object>>, TickerBo> {

    @Override
    @Nonnull
    public TickerBo convert(@Nonnull final Map.Entry<String, Map<String, Object>> inputEntry) {
        @SuppressWarnings("unchecked") final List<String> askArray = (List<String>) inputEntry.getValue().get("a");
        @SuppressWarnings("unchecked") final List<String> bidArray = (List<String>) inputEntry.getValue().get("b");
        return new TickerBo(
                inputEntry.getKey(),
                new BigDecimal(askArray.get(0)),
                new BigDecimal(bidArray.get(0))
        );
    }
}
