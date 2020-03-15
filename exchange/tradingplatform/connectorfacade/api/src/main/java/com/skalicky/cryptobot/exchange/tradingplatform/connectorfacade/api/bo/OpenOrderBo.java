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
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class OpenOrderBo {
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
    /**
     * Null in case of "market" price order type.
     */
    @Nullable
    private final BigDecimal desiredPrice;
    @Nonnull
    private final LocalDateTime openDateTime;
    @Nullable
    private final LocalDateTime expirationDateTime;
    @Nonnull
    private final OrderStateBoEnum state;
    @Nonnull
    private final BigDecimal alreadyExecutedVolumeInQuoteCurrency;
    /**
     * Null in case there are no trades yet.
     */
    @Nullable
    private final BigDecimal averageActualPrice;
    /**
     * Null in case there are no trades yet.
     */
    @Nullable
    private final BigDecimal actualFeeInQuoteCurrency;
    @Nonnull
    private final ImmutableList<String> tradeIds;

    public OpenOrderBo(@Nonnull final String orderId,
                       @Nonnull final OrderTypeBoEnum orderType,
                       @Nonnull final PriceOrderTypeBoEnum priceOrderType,
                       @Nonnull final CurrencyPairBo currencyPair,
                       @Nonnull final BigDecimal desiredVolumeInQuoteCurrency,
                       @Nullable final BigDecimal desiredPrice,
                       @Nonnull final LocalDateTime openDateTime,
                       @Nullable final LocalDateTime expirationDateTime,
                       @Nonnull final OrderStateBoEnum state,
                       @Nonnull final BigDecimal alreadyExecutedVolumeInQuoteCurrency,
                       @Nullable final BigDecimal averageActualPrice,
                       @Nullable final BigDecimal actualFeeInQuoteCurrency,
                       @Nonnull final ImmutableList<String> tradeIds) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.priceOrderType = priceOrderType;
        this.currencyPair = currencyPair;
        this.desiredVolumeInQuoteCurrency = desiredVolumeInQuoteCurrency;
        this.desiredPrice = desiredPrice;
        this.openDateTime = openDateTime;
        this.expirationDateTime = expirationDateTime;
        this.state = state;
        this.alreadyExecutedVolumeInQuoteCurrency = alreadyExecutedVolumeInQuoteCurrency;
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

    @Nullable
    public BigDecimal getDesiredPrice() {
        return desiredPrice;
    }

    @Nonnull
    public LocalDateTime getOpenDateTime() {
        return openDateTime;
    }

    @Nullable
    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    @Nonnull
    public OrderStateBoEnum getState() {
        return state;
    }

    @Nonnull
    public BigDecimal getAlreadyExecutedVolumeInQuoteCurrency() {
        return alreadyExecutedVolumeInQuoteCurrency;
    }

    @Nullable
    public BigDecimal getAverageActualPrice() {
        return averageActualPrice;
    }

    @Nullable
    public BigDecimal getActualFeeInQuoteCurrency() {
        return actualFeeInQuoteCurrency;
    }

    @Nonnull
    public ImmutableList<String> getTradeIds() {
        return tradeIds;
    }
}
