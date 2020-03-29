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
    private KrakenOrderDescriptionDto descr = KrakenOrderDescriptionDtoBuilder.aKrakenOrderDescriptionDto().build();
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

    @NotNull
    public static KrakenClosedOrderDtoBuilder aKrakenClosedOrderDto() {
        return new KrakenClosedOrderDtoBuilder();
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withRefid(@Nullable final String refid) {
        this.refid = refid;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withUserref(@Nullable final Long userref) {
        this.userref = userref;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withStatus(@Nullable final String status) {
        this.status = status;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withReason(@Nullable final String reason) {
        this.reason = reason;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withOpentm(@Nullable final BigDecimal opentm) {
        this.opentm = opentm;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withClosetm(@Nullable final BigDecimal closetm) {
        this.closetm = closetm;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withStarttm(@Nullable final BigDecimal starttm) {
        this.starttm = starttm;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withExpiretm(@Nullable final BigDecimal expiretm) {
        this.expiretm = expiretm;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withDescr(@Nullable final KrakenOrderDescriptionDto descr) {
        this.descr = descr;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withVol(@Nullable final BigDecimal vol) {
        this.vol = vol;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withVol_exec(@Nullable final BigDecimal vol_exec) {
        this.vol_exec = vol_exec;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withCost(@Nullable final BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withFee(@Nullable final BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withPrice(@Nullable final BigDecimal price) {
        this.price = price;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withStopprice(@Nullable final BigDecimal stopprice) {
        this.stopprice = stopprice;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withLimitprice(@Nullable final BigDecimal limitprice) {
        this.limitprice = limitprice;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withMisc(@Nullable final String misc) {
        this.misc = misc;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withOflags(@Nullable final String oflags) {
        this.oflags = oflags;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDtoBuilder withTrades(@Nullable final List<String> trades) {
        this.trades = trades;
        return this;
    }

    @NotNull
    public KrakenClosedOrderDto build() {
        final var krakenClosedOrderDto = new KrakenClosedOrderDto();
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
