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

import java.math.BigDecimal;
import java.util.List;

public class KrakenClosedOrderDto {
    @Nullable
    private String refid;
    @Nullable
    private Long userref;
    @Nullable
    private String status;
    @Nullable
    private String reason;
    @Nullable
    private BigDecimal opentm;
    @Nullable
    private BigDecimal closetm;
    @Nullable
    private BigDecimal starttm;
    @Nullable
    private BigDecimal expiretm;
    @Nullable
    private KrakenOrderDescriptionDto descr;
    @Nullable
    private BigDecimal vol;
    @Nullable
    private BigDecimal vol_exec;
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

    @Nullable
    public String getRefid() {
        return refid;
    }

    public void setRefid(@Nullable final String refid) {
        this.refid = refid;
    }

    @Nullable
    public Long getUserref() {
        return userref;
    }

    public void setUserref(@Nullable final Long userref) {
        this.userref = userref;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable final String status) {
        this.status = status;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(@Nullable final String reason) {
        this.reason = reason;
    }

    @Nullable
    public BigDecimal getOpentm() {
        return opentm;
    }

    public void setOpentm(@Nullable final BigDecimal opentm) {
        this.opentm = opentm;
    }

    @Nullable
    public BigDecimal getClosetm() {
        return closetm;
    }

    public void setClosetm(@Nullable final BigDecimal closetm) {
        this.closetm = closetm;
    }

    @Nullable
    public BigDecimal getStarttm() {
        return starttm;
    }

    public void setStarttm(@Nullable final BigDecimal starttm) {
        this.starttm = starttm;
    }

    @Nullable
    public BigDecimal getExpiretm() {
        return expiretm;
    }

    public void setExpiretm(@Nullable final BigDecimal expiretm) {
        this.expiretm = expiretm;
    }

    @Nullable
    public KrakenOrderDescriptionDto getDescr() {
        return descr;
    }

    public void setDescr(@Nullable final KrakenOrderDescriptionDto descr) {
        this.descr = descr;
    }

    @Nullable
    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(@Nullable final BigDecimal vol) {
        this.vol = vol;
    }

    @Nullable
    public BigDecimal getVol_exec() {
        return vol_exec;
    }

    public void setVol_exec(@Nullable final BigDecimal vol_exec) {
        this.vol_exec = vol_exec;
    }

    @Nullable
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(@Nullable final BigDecimal cost) {
        this.cost = cost;
    }

    @Nullable
    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(@Nullable final BigDecimal fee) {
        this.fee = fee;
    }

    @Nullable
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@Nullable final BigDecimal price) {
        this.price = price;
    }

    @Nullable
    public BigDecimal getStopprice() {
        return stopprice;
    }

    public void setStopprice(@Nullable final BigDecimal stopprice) {
        this.stopprice = stopprice;
    }

    @Nullable
    public BigDecimal getLimitprice() {
        return limitprice;
    }

    public void setLimitprice(@Nullable final BigDecimal limitprice) {
        this.limitprice = limitprice;
    }

    @Nullable
    public String getMisc() {
        return misc;
    }

    public void setMisc(@Nullable final String misc) {
        this.misc = misc;
    }

    @Nullable
    public String getOflags() {
        return oflags;
    }

    public void setOflags(@Nullable final String oflags) {
        this.oflags = oflags;
    }

    @Nullable
    public List<String> getTrades() {
        return trades;
    }

    public void setTrades(@Nullable final List<String> trades) {
        this.trades = trades;
    }
}
