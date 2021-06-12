/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2021 Tomas Skalicky
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

package com.skalicky.cryptobot.businesslogic.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

public class VolumeMultiplierBo {
    @Nullable
    private final BigDecimal upperBoundPriceInBaseCurrency;
    @NotNull
    private final BigDecimal volumeMultiplierInDecimal;

    public VolumeMultiplierBo(final @Nullable BigDecimal upperBoundPriceInBaseCurrency,
                              final @NotNull BigDecimal volumeMultiplierInDecimal) {
        this.upperBoundPriceInBaseCurrency = upperBoundPriceInBaseCurrency;
        this.volumeMultiplierInDecimal = volumeMultiplierInDecimal;
    }

    @Nullable
    public BigDecimal getUpperBoundPriceInBaseCurrency() {
        return upperBoundPriceInBaseCurrency;
    }

    @NotNull
    public BigDecimal getVolumeMultiplierInDecimal() {
        return volumeMultiplierInDecimal;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VolumeMultiplierBo that = (VolumeMultiplierBo) o;
        return Objects.equals(upperBoundPriceInBaseCurrency,
                that.upperBoundPriceInBaseCurrency) && volumeMultiplierInDecimal.equals(that.volumeMultiplierInDecimal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upperBoundPriceInBaseCurrency, volumeMultiplierInDecimal);
    }
}
