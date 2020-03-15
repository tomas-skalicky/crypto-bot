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

import javax.annotation.Nullable;
import java.math.BigDecimal;

public class KrakenOrderDescriptionDto {
    @Nullable
    private String pair;
    @Nullable
    private String type;
    @Nullable
    private String ordertype;
    @Nullable
    private BigDecimal price;
    @Nullable
    private BigDecimal price2;
    @Nullable
    private String leverage;
    @Nullable
    private String order;
    @Nullable
    private String close;

    @Nullable
    public String getPair() {
        return pair;
    }

    public void setPair(@Nullable final String pair) {
        this.pair = pair;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setType(@Nullable final String type) {
        this.type = type;
    }

    @Nullable
    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(@Nullable final String ordertype) {
        this.ordertype = ordertype;
    }

    @Nullable
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@Nullable final BigDecimal price) {
        this.price = price;
    }

    @Nullable
    public BigDecimal getPrice2() {
        return price2;
    }

    public void setPrice2(@Nullable final BigDecimal price2) {
        this.price2 = price2;
    }

    @Nullable
    public String getLeverage() {
        return leverage;
    }

    public void setLeverage(@Nullable final String leverage) {
        this.leverage = leverage;
    }

    @Nullable
    public String getOrder() {
        return order;
    }

    public void setOrder(@Nullable final String order) {
        this.order = order;
    }

    @Nullable
    public String getClose() {
        return close;
    }

    public void setClose(@Nullable final String close) {
        this.close = close;
    }
}
