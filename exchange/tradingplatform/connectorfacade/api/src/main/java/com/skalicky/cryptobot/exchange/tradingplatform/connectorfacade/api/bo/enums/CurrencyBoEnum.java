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

package com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CurrencyBoEnum {

    // @formatter:off
    BTC   ("BTC"),
    EUR   ("EUR"),
    OTHERS(null),
    ;
    // @formatter:on

    @NotNull
    private static final Map<String, CurrencyBoEnum> enumValuesByLabels = Arrays.stream(CurrencyBoEnum.values())
            .collect(Collectors.toMap(CurrencyBoEnum::getLabel, Function.identity()));

    @Nullable
    private final String label;

    CurrencyBoEnum(@Nullable final String label) {
        this.label = label;
    }

    @Nullable
    public String getLabel() {
        return label;
    }

    @NotNull
    public static CurrencyBoEnum getByLabel(@Nullable final String label) {
        if (enumValuesByLabels.containsKey(label)) {
            return enumValuesByLabels.get(label);
        } else {
            throw new IllegalArgumentException("Unsupported label [" + label + "]");
        }
    }
}
