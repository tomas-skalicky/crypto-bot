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

public final class KrakenClosedOrderDtoBuilder {
    @Nullable
    private String refid;
    @Nullable
    private Long userref;
    @Nullable
    private String status = "closed";
    @Nullable
    private String reason;
    @Nullable
    private BigDecimal opentm = BigDecimal.valueOf(1583831100);
    @Nullable
    private BigDecimal closetm = BigDecimal.valueOf(1583831200);
    @Nullable
    private BigDecimal starttm;
    @Nullable
    private BigDecimal expiretm;
    @Nullable
    private KrakenClosedOrderDescriptionDto descr = KrakenClosedOrderDescriptionDtoBuilder.aKrakenClosedOrderDescriptionDto().build();
    @Nullable
    private BigDecimal vol = new BigDecimal("0.56");
    @Nullable
    private BigDecimal vol_exec = new BigDecimal("0.56");
    @Nullable
    private BigDecimal cost = BigDecimal.ZERO;
    @Nullable
    private BigDecimal fee = BigDecimal.valueOf(45);
    @Nullable
    private BigDecimal price = new BigDecimal("4567.8");
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

    private KrakenClosedOrderDtoBuilder() {
    }

    @Nonnull
    public static KrakenClosedOrderDtoBuilder aKrakenClosedOrderDto() {
        return new KrakenClosedOrderDtoBuilder();
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withRefid(@Nullable final String refid) {
        this.refid = refid;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withUserref(@Nullable final Long userref) {
        this.userref = userref;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withStatus(@Nullable final String status) {
        this.status = status;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withReason(@Nullable final String reason) {
        this.reason = reason;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withOpentm(@Nullable final BigDecimal opentm) {
        this.opentm = opentm;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withClosetm(@Nullable final BigDecimal closetm) {
        this.closetm = closetm;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withStarttm(@Nullable final BigDecimal starttm) {
        this.starttm = starttm;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withExpiretm(@Nullable final BigDecimal expiretm) {
        this.expiretm = expiretm;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withDescr(@Nullable final KrakenClosedOrderDescriptionDto descr) {
        this.descr = descr;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withVol(@Nullable final BigDecimal vol) {
        this.vol = vol;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withVol_exec(@Nullable final BigDecimal vol_exec) {
        this.vol_exec = vol_exec;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withCost(@Nullable final BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withFee(@Nullable final BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withPrice(@Nullable final BigDecimal price) {
        this.price = price;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withStopprice(@Nullable final BigDecimal stopprice) {
        this.stopprice = stopprice;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withLimitprice(@Nullable final BigDecimal limitprice) {
        this.limitprice = limitprice;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withMisc(@Nullable final String misc) {
        this.misc = misc;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withOflags(@Nullable final String oflags) {
        this.oflags = oflags;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDtoBuilder withTrades(@Nullable final List<String> trades) {
        this.trades = trades;
        return this;
    }

    @Nonnull
    public KrakenClosedOrderDto build() {
        final KrakenClosedOrderDto krakenClosedOrderDto = new KrakenClosedOrderDto();
        krakenClosedOrderDto.setRefid(refid);
        krakenClosedOrderDto.setUserref(userref);
        krakenClosedOrderDto.setStatus(status);
        krakenClosedOrderDto.setReason(reason);
        krakenClosedOrderDto.setOpentm(opentm);
        krakenClosedOrderDto.setClosetm(closetm);
        krakenClosedOrderDto.setStarttm(starttm);
        krakenClosedOrderDto.setExpiretm(expiretm);
        krakenClosedOrderDto.setDescr(descr);
        krakenClosedOrderDto.setVol(vol);
        krakenClosedOrderDto.setVol_exec(vol_exec);
        krakenClosedOrderDto.setCost(cost);
        krakenClosedOrderDto.setFee(fee);
        krakenClosedOrderDto.setPrice(price);
        krakenClosedOrderDto.setStopprice(stopprice);
        krakenClosedOrderDto.setLimitprice(limitprice);
        krakenClosedOrderDto.setMisc(misc);
        krakenClosedOrderDto.setOflags(oflags);
        krakenClosedOrderDto.setTrades(trades);
        return krakenClosedOrderDto;
    }
}
