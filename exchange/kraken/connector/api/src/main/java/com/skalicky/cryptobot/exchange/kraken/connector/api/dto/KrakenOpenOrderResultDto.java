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

package com.skalicky.cryptobot.exchange.kraken.connector.api.dto;

import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class KrakenOpenOrderResultDto {
    @Nullable
    private Map<String, KrakenOpenOrderDto> open;

    @Nullable
    public Map<String, KrakenOpenOrderDto> getOpen() {
        return open;
    }

    public void setOpen(@Nullable final Map<String, KrakenOpenOrderDto> open) {
        this.open = open;
    }
}
