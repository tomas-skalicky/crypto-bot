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

import com.skalicky.cryptobot.businesslogic.api.CryptoBotLogic;
import com.skalicky.cryptobot.exchange.shared.facade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.shared.facade.api.logic.TradingPlatformDesignated;
import com.skalicky.cryptobot.exchange.shared.facade.api.logic.TradingPlatformPublicApiFacade;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CryptoBotLogicBean implements CryptoBotLogic {

    @Nonnull
    private final Map<String, TradingPlatformPublicApiFacade> publicApiFacadesByPlatformNames;

    public CryptoBotLogicBean(@Nonnull final List<TradingPlatformPublicApiFacade> publicApiFacades) {
        this.publicApiFacadesByPlatformNames = publicApiFacades.stream()
                .collect(Collectors.toMap(TradingPlatformDesignated::getTradingPlatform, Function.identity()));
    }

    @Override
    public void placeBuyOrder(@Nonnull final String tradingPlatformName) {
        final TradingPlatformPublicApiFacade publicApiFacade = publicApiFacadesByPlatformNames.get(tradingPlatformName);
        if (publicApiFacade == null) {
            throw new IllegalArgumentException("No public API facade for the trading platform " + tradingPlatformName);
        }

        TickerBo ticket = publicApiFacade.getTicker("XBTEUR");
        // FIXME Tomas remove
        System.out.println(ticket);
    }
}
