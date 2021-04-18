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

package com.skalicky.cryptobot.businesslogic.api;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface CryptoBotOrchestratingLogic {

    void orchestrateExecution(@NotNull String tradingPlatformName,
                              @NotNull BigDecimal volumeInBaseCurrencyToInvestPerRun,
                              @NotNull String baseCurrencyLabel,
                              @NotNull String quoteCurrencyLabel,
                              @NotNull BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal,
                              final int minOffsetFromOpenDateTimeOfLastBuyOrderInHours);
}
