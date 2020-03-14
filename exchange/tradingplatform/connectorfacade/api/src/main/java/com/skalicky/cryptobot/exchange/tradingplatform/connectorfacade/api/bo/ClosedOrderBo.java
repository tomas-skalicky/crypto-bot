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

package com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo;

import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ClosedOrderBo {
    @Nonnull
    private final String orderId;
    @Nonnull
    private final OrderTypeBoEnum orderType;
    @Nonnull
    private final PriceOrderTypeBoEnum priceOrderType;
    @Nonnull
    private final CurrencyPairBo currencyPair;
    @Nonnull
    private final BigDecimal desiredVolumeInQuoteCurrency;
    @Nonnull
    private final BigDecimal desiredPrice;
    @Nonnull
    private final LocalDateTime openDateTime;
    @Nonnull
    private final LocalDateTime closeDateTime;
    @Nonnull
    private final OrderStateBoEnum status;
    @Nonnull
    private final BigDecimal totalExecutedVolumeInQuoteCurrency;
    @Nonnull
    private final BigDecimal averageActualPrice;
    @Nonnull
    private final BigDecimal actualFeeInQuoteCurrency;
    @Nonnull
    private final ImmutableList<String> tradeIds;

    public ClosedOrderBo(@Nonnull final String orderId,
                         @Nonnull final OrderTypeBoEnum orderType,
                         @Nonnull final PriceOrderTypeBoEnum priceOrderType,
                         @Nonnull final CurrencyPairBo currencyPair,
                         @Nonnull final BigDecimal desiredVolumeInQuoteCurrency,
                         @Nonnull final BigDecimal desiredPrice,
                         @Nonnull final LocalDateTime openDateTime,
                         @Nonnull final LocalDateTime closeDateTime,
                         @Nonnull final OrderStateBoEnum status,
                         @Nonnull final BigDecimal totalExecutedVolumeInQuoteCurrency,
                         @Nonnull final BigDecimal averageActualPrice,
                         @Nonnull final BigDecimal actualFeeInQuoteCurrency,
                         @Nonnull final ImmutableList<String> tradeIds) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.priceOrderType = priceOrderType;
        this.currencyPair = currencyPair;
        this.desiredVolumeInQuoteCurrency = desiredVolumeInQuoteCurrency;
        this.desiredPrice = desiredPrice;
        this.openDateTime = openDateTime;
        this.closeDateTime = closeDateTime;
        this.status = status;
        this.totalExecutedVolumeInQuoteCurrency = totalExecutedVolumeInQuoteCurrency;
        this.averageActualPrice = averageActualPrice;
        this.actualFeeInQuoteCurrency = actualFeeInQuoteCurrency;
        this.tradeIds = tradeIds;
    }

    @Nonnull
    public String getOrderId() {
        return orderId;
    }

    @Nonnull
    public OrderTypeBoEnum getOrderType() {
        return orderType;
    }

    @Nonnull
    public PriceOrderTypeBoEnum getPriceOrderType() {
        return priceOrderType;
    }

    @Nonnull
    public CurrencyPairBo getCurrencyPair() {
        return currencyPair;
    }

    @Nonnull
    public BigDecimal getDesiredVolumeInQuoteCurrency() {
        return desiredVolumeInQuoteCurrency;
    }

    @Nonnull
    public BigDecimal getDesiredPrice() {
        return desiredPrice;
    }

    @Nonnull
    public LocalDateTime getOpenDateTime() {
        return openDateTime;
    }

    @Nonnull
    public LocalDateTime getCloseDateTime() {
        return closeDateTime;
    }

    @Nonnull
    public OrderStateBoEnum getStatus() {
        return status;
    }

    @Nonnull
    public BigDecimal getTotalExecutedVolumeInQuoteCurrency() {
        return totalExecutedVolumeInQuoteCurrency;
    }

    @Nonnull
    public BigDecimal getAverageActualPrice() {
        return averageActualPrice;
    }

    @Nonnull
    public BigDecimal getActualFeeInQuoteCurrency() {
        return actualFeeInQuoteCurrency;
    }

    @Nonnull
    public ImmutableList<String> getTradeIds() {
        return tradeIds;
    }
}
