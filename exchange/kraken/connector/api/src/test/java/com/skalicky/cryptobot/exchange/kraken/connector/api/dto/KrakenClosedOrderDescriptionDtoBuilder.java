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

public final class KrakenClosedOrderDescriptionDtoBuilder {
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

    private KrakenClosedOrderDescriptionDtoBuilder() {
    }

    @Nonnull
    public static KrakenClosedOrderDescriptionDtoBuilder aKrakenClosedOrderDescriptionDto() {
        return new KrakenClosedOrderDescriptionDtoBuilder();
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withPair(@Nonnull final String pair) {
        this.pair = pair;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withType(@Nonnull final String type) {
        this.type = type;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withOrdertype(@Nonnull final String ordertype) {
        this.ordertype = ordertype;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withPrice(@Nonnull final BigDecimal price) {
        this.price = price;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withPrice2(@Nonnull final BigDecimal price2) {
        this.price2 = price2;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withLeverage(@Nonnull final String leverage) {
        this.leverage = leverage;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withOrder(@Nonnull final String order) {
        this.order = order;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDtoBuilder withClose(@Nonnull final String close) {
        this.close = close;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDescriptionDto build() {
        final KrakenClosedOrderDescriptionDto krakenClosedOrderDescriptionDto = new KrakenClosedOrderDescriptionDto();
        krakenClosedOrderDescriptionDto.setPair(pair);
        krakenClosedOrderDescriptionDto.setType(type);
        krakenClosedOrderDescriptionDto.setOrdertype(ordertype);
        krakenClosedOrderDescriptionDto.setPrice(price);
        krakenClosedOrderDescriptionDto.setPrice2(price2);
        krakenClosedOrderDescriptionDto.setLeverage(leverage);
        krakenClosedOrderDescriptionDto.setOrder(order);
        krakenClosedOrderDescriptionDto.setClose(close);
        return krakenClosedOrderDescriptionDto;
    }
}
