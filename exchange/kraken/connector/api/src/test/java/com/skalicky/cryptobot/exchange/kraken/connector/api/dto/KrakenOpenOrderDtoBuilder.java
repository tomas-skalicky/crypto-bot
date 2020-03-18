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
import java.util.List;

public final class KrakenOpenOrderDtoBuilder {
    @Nullable
    private String refid;
    @Nullable
    private Long userref;
    @Nullable
    private String status = "open";
    @Nullable
    private BigDecimal opentm = BigDecimal.valueOf(1583831100);
    @Nullable
    private BigDecimal starttm;
    @Nullable
    private BigDecimal expiretm;
    @Nullable
    private KrakenOrderDescriptionDto descr = KrakenOrderDescriptionDtoBuilder.aKrakenOrderDescriptionDto().build();
    @Nullable
    private BigDecimal vol = new BigDecimal("0.56");
    @Nullable
    private BigDecimal vol_exec = BigDecimal.ZERO;
    @Nullable
    private BigDecimal cost;
    @Nullable
    private BigDecimal fee;
    @Nullable
    private BigDecimal price;
    @Nullable
    private BigDecimal stopprice;
    @Nullable
    private BigDecimal limitprice;
    @Nullable
    private String misc;
    @Nullable
    private String oflags;
    @Nullable
    private List<String> trades;

    private KrakenOpenOrderDtoBuilder() {
    }

    @Nonnull
    public static KrakenOpenOrderDtoBuilder aKrakenOpenOrderDto() {
        return new KrakenOpenOrderDtoBuilder();
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withRefid(@Nullable final String refid) {
        this.refid = refid;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withUserref(@Nullable final Long userref) {
        this.userref = userref;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withStatus(@Nullable final String status) {
        this.status = status;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withOpentm(@Nullable final BigDecimal opentm) {
        this.opentm = opentm;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withStarttm(@Nullable final BigDecimal starttm) {
        this.starttm = starttm;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withExpiretm(@Nullable final BigDecimal expiretm) {
        this.expiretm = expiretm;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withDescr(@Nullable final KrakenOrderDescriptionDto descr) {
        this.descr = descr;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withVol(@Nullable final BigDecimal vol) {
        this.vol = vol;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withVol_exec(@Nullable final BigDecimal vol_exec) {
        this.vol_exec = vol_exec;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withCost(@Nullable final BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withFee(@Nullable final BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withPrice(@Nullable final BigDecimal price) {
        this.price = price;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withStopprice(@Nullable final BigDecimal stopprice) {
        this.stopprice = stopprice;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withLimitprice(@Nullable final BigDecimal limitprice) {
        this.limitprice = limitprice;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withMisc(@Nullable final String misc) {
        this.misc = misc;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withOflags(@Nullable final String oflags) {
        this.oflags = oflags;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDtoBuilder withTrades(@Nullable final List<String> trades) {
        this.trades = trades;
        return this;
    }

    @Nonnull
    public KrakenOpenOrderDto build() {
        final var krakenOpenOrderDto = new KrakenOpenOrderDto();
        krakenOpenOrderDto.setRefid(refid);
        krakenOpenOrderDto.setUserref(userref);
        krakenOpenOrderDto.setStatus(status);
        krakenOpenOrderDto.setOpentm(opentm);
        krakenOpenOrderDto.setStarttm(starttm);
        krakenOpenOrderDto.setExpiretm(expiretm);
        krakenOpenOrderDto.setDescr(descr);
        krakenOpenOrderDto.setVol(vol);
        krakenOpenOrderDto.setVol_exec(vol_exec);
        krakenOpenOrderDto.setCost(cost);
        krakenOpenOrderDto.setFee(fee);
        krakenOpenOrderDto.setPrice(price);
        krakenOpenOrderDto.setStopprice(stopprice);
        krakenOpenOrderDto.setLimitprice(limitprice);
        krakenOpenOrderDto.setMisc(misc);
        krakenOpenOrderDto.setOflags(oflags);
        krakenOpenOrderDto.setTrades(trades);
        return krakenOpenOrderDto;
    }
}
