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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nonnull
    public static KrakenOrderDescriptionDtoBuilder aKrakenOrderDescriptionDto() {
        return new KrakenOrderDescriptionDtoBuilder();
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withPair(@Nonnull final String pair) {
        this.pair = pair;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withType(@Nonnull final String type) {
        this.type = type;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withOrdertype(@Nonnull final String ordertype) {
        this.ordertype = ordertype;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withPrice(@Nonnull final BigDecimal price) {
        this.price = price;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withPrice2(@Nonnull final BigDecimal price2) {
        this.price2 = price2;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withLeverage(@Nonnull final String leverage) {
        this.leverage = leverage;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withOrder(@Nonnull final String order) {
        this.order = order;
        return this;
    }

    @Nonnull
    public KrakenOrderDescriptionDtoBuilder withClose(@Nonnull final String close) {
        this.close = close;
        return this;
    }

    @Nonnull
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
