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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;

public final class KrakenOrderDescriptionDtoBuilder {
    @Nullable
    private String pair = "XBTEUR";
    @Nullable
    private String type = "buy";
    @Nullable
    private String ordertype = "limit";
    @Nullable
    private BigDecimal price = BigDecimal.valueOf(4570);
    @Nullable
    private BigDecimal price2;
    @Nullable
    private String leverage;
    @Nullable
    private String order;
    @Nullable
    private String close;

    private KrakenOrderDescriptionDtoBuilder() {
    }

    @NotNull
    public static KrakenOrderDescriptionDtoBuilder aKrakenOrderDescriptionDto() {
        return new KrakenOrderDescriptionDtoBuilder();
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withPair(@NotNull final String pair) {
        this.pair = pair;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withType(@NotNull final String type) {
        this.type = type;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withOrdertype(@NotNull final String ordertype) {
        this.ordertype = ordertype;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withPrice(@NotNull final BigDecimal price) {
        this.price = price;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withPrice2(@NotNull final BigDecimal price2) {
        this.price2 = price2;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withLeverage(@NotNull final String leverage) {
        this.leverage = leverage;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withOrder(@NotNull final String order) {
        this.order = order;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDtoBuilder withClose(@NotNull final String close) {
        this.close = close;
        return this;
    }

    @NotNull
    public KrakenOrderDescriptionDto build() {
        final var krakenOrderDescriptionDto = new KrakenOrderDescriptionDto();
        krakenOrderDescriptionDto.setPair(pair);
        krakenOrderDescriptionDto.setType(type);
        krakenOrderDescriptionDto.setOrdertype(ordertype);
        krakenOrderDescriptionDto.setPrice(price);
        krakenOrderDescriptionDto.setPrice2(price2);
        krakenOrderDescriptionDto.setLeverage(leverage);
        krakenOrderDescriptionDto.setOrder(order);
        krakenOrderDescriptionDto.setClose(close);
        return krakenOrderDescriptionDto;
    }
}
