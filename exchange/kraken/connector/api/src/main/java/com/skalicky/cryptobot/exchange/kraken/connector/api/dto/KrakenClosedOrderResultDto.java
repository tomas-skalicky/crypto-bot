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

public class KrakenClosedOrderResultDto {
    @Nullable
    private Map<String, KrakenClosedOrderDto> closed;
    @Nullable
    private Long count;

    @Nullable
    public Map<String, KrakenClosedOrderDto> getClosed() {
        return closed;
    }

    public void setClosed(@Nullable final Map<String, KrakenClosedOrderDto> closed) {
        this.closed = closed;
    }

    @Nullable
    public Long getCount() {
        return count;
    }

    public void setCount(@Nullable final Long count) {
        this.count = count;
    }
}
