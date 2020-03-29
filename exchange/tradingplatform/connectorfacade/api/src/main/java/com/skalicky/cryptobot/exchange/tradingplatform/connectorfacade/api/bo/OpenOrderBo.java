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

public final class OpenOrderBo {
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
    @Nullable
    private final LocalDateTime expirationDateTime;
    @NotNull
    private final OrderStateBoEnum state;
    @NotNull
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
    @NotNull
    private final ImmutableList<String> tradeIds;

    public OpenOrderBo(@NotNull final String orderId,
                       @NotNull final OrderTypeBoEnum orderType,
                       @NotNull final PriceOrderTypeBoEnum priceOrderType,
                       @NotNull final CurrencyPairBo currencyPair,
                       @NotNull final BigDecimal desiredVolumeInQuoteCurrency,
                       @Nullable final BigDecimal desiredPrice,
                       @NotNull final LocalDateTime openDateTime,
                       @Nullable final LocalDateTime expirationDateTime,
                       @NotNull final OrderStateBoEnum state,
                       @NotNull final BigDecimal alreadyExecutedVolumeInQuoteCurrency,
                       @Nullable final BigDecimal averageActualPrice,
                       @Nullable final BigDecimal actualFeeInQuoteCurrency,
                       @NotNull final ImmutableList<String> tradeIds) {
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

    @Nullable
    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    @NotNull
    public OrderStateBoEnum getState() {
        return state;
    }

    @NotNull
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

    @NotNull
    public ImmutableList<String> getTradeIds() {
        return tradeIds;
    }
}
