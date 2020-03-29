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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ClosedOrderBo {
    @NotNull
    private final String orderId;
    @NotNull
    private final OrderTypeBoEnum orderType;
    @NotNull
    private final PriceOrderTypeBoEnum priceOrderType;
    @NotNull
    private final CurrencyPairBo currencyPair;
    @NotNull
    private final BigDecimal desiredVolumeInQuoteCurrency;
    /**
     * Null in case of "market" price order type.
     */
    @Nullable
    private final BigDecimal desiredPrice;
    @NotNull
    private final LocalDateTime openDateTime;
    @NotNull
    private final LocalDateTime closeDateTime;
    @NotNull
    private final OrderStateBoEnum status;
    @NotNull
    private final BigDecimal totalExecutedVolumeInQuoteCurrency;
    @NotNull
    private final BigDecimal averageActualPrice;
    @NotNull
    private final BigDecimal actualFeeInQuoteCurrency;
    @NotNull
    private final ImmutableList<String> tradeIds;

    public ClosedOrderBo(@NotNull final String orderId,
                         @NotNull final OrderTypeBoEnum orderType,
                         @NotNull final PriceOrderTypeBoEnum priceOrderType,
                         @NotNull final CurrencyPairBo currencyPair,
                         @NotNull final BigDecimal desiredVolumeInQuoteCurrency,
                         @Nullable final BigDecimal desiredPrice,
                         @NotNull final LocalDateTime openDateTime,
                         @NotNull final LocalDateTime closeDateTime,
                         @NotNull final OrderStateBoEnum status,
                         @NotNull final BigDecimal totalExecutedVolumeInQuoteCurrency,
                         @NotNull final BigDecimal averageActualPrice,
                         @NotNull final BigDecimal actualFeeInQuoteCurrency,
                         @NotNull final ImmutableList<String> tradeIds) {
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

    @NotNull
    public String getOrderId() {
        return orderId;
    }

    @NotNull
    public OrderTypeBoEnum getOrderType() {
        return orderType;
    }

    @NotNull
    public PriceOrderTypeBoEnum getPriceOrderType() {
        return priceOrderType;
    }

    @NotNull
    public CurrencyPairBo getCurrencyPair() {
        return currencyPair;
    }

    @NotNull
    public BigDecimal getDesiredVolumeInQuoteCurrency() {
        return desiredVolumeInQuoteCurrency;
    }

    @Nullable
    public BigDecimal getDesiredPrice() {
        return desiredPrice;
    }

    @NotNull
    public LocalDateTime getOpenDateTime() {
        return openDateTime;
    }

    @NotNull
    public LocalDateTime getCloseDateTime() {
        return closeDateTime;
    }

    @NotNull
    public OrderStateBoEnum getStatus() {
        return status;
    }

    @NotNull
    public BigDecimal getTotalExecutedVolumeInQuoteCurrency() {
        return totalExecutedVolumeInQuoteCurrency;
    }

    @NotNull
    public BigDecimal getAverageActualPrice() {
        return averageActualPrice;
    }

    @NotNull
    public BigDecimal getActualFeeInQuoteCurrency() {
        return actualFeeInQuoteCurrency;
    }

    @NotNull
    public ImmutableList<String> getTradeIds() {
        return tradeIds;
    }
}
