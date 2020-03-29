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
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ClosedOrderBoBuilder {
    @NotNull
    private String orderId = "";
    @NotNull
    private OrderTypeBoEnum orderType = OrderTypeBoEnum.BUY;
    @NotNull
    private PriceOrderTypeBoEnum priceOrderType = PriceOrderTypeBoEnum.LIMIT;
    @NotNull
    private CurrencyPairBo currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
    @NotNull
    private BigDecimal desiredVolumeInQuoteCurrency = new BigDecimal("0.56");
    @Nullable
    private BigDecimal desiredPrice = BigDecimal.valueOf(4988);
    @NotNull
    private LocalDateTime openDateTime = LocalDateTime.of(2020, 3, 13, 21, 0);
    @NotNull
    private LocalDateTime closeDateTime = LocalDateTime.of(2020, 3, 14, 10, 0);
    @NotNull
    private OrderStateBoEnum status = OrderStateBoEnum.FULLY_EXECUTED;
    @NotNull
    private BigDecimal totalExecutedVolumeInQuoteCurrency = new BigDecimal("0.56");
    @NotNull
    private BigDecimal averageActualPrice = BigDecimal.valueOf(4985);
    @NotNull
    private BigDecimal actualFeeInQuoteCurrency = BigDecimal.ZERO;
    @NotNull
    private ImmutableList<String> tradeIds = ImmutableList.<String>builder().build();

    private ClosedOrderBoBuilder() {
    }

    @NotNull
    public static ClosedOrderBoBuilder aClosedOrderBo() {
        return new ClosedOrderBoBuilder();
    }

    @NotNull
    public ClosedOrderBoBuilder withOrderId(@NotNull final String orderId) {
        this.orderId = orderId;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withOrderType(@NotNull final OrderTypeBoEnum orderType) {
        this.orderType = orderType;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withPriceOrderType(@NotNull final PriceOrderTypeBoEnum priceOrderType) {
        this.priceOrderType = priceOrderType;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withCurrencyPair(@NotNull final CurrencyPairBo currencyPair) {
        this.currencyPair = currencyPair;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withDesiredVolumeInQuoteCurrency(@NotNull final BigDecimal desiredVolumeInQuoteCurrency) {
        this.desiredVolumeInQuoteCurrency = desiredVolumeInQuoteCurrency;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withDesiredPrice(@Nullable final BigDecimal desiredPrice) {
        this.desiredPrice = desiredPrice;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withOpenDateTime(@NotNull final LocalDateTime openDateTime) {
        this.openDateTime = openDateTime;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withCloseDateTime(@NotNull final LocalDateTime closeDateTime) {
        this.closeDateTime = closeDateTime;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withStatus(@NotNull final OrderStateBoEnum status) {
        this.status = status;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withTotalExecutedVolumeInQuoteCurrency(@NotNull final BigDecimal totalExecutedVolumeInQuoteCurrency) {
        this.totalExecutedVolumeInQuoteCurrency = totalExecutedVolumeInQuoteCurrency;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withAverageActualPrice(@NotNull final BigDecimal averageActualPrice) {
        this.averageActualPrice = averageActualPrice;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withActualFeeInQuoteCurrency(@NotNull final BigDecimal actualFeeInQuoteCurrency) {
        this.actualFeeInQuoteCurrency = actualFeeInQuoteCurrency;
        return this;
    }

    @NotNull
    public ClosedOrderBoBuilder withTradeIds(@NotNull final ImmutableList<String> tradeIds) {
        this.tradeIds = tradeIds;
        return this;
    }

    @NotNull
    public ClosedOrderBo build() {
        return new ClosedOrderBo(orderId, orderType, priceOrderType, currencyPair, desiredVolumeInQuoteCurrency,
                desiredPrice, openDateTime, closeDateTime, status, totalExecutedVolumeInQuoteCurrency,
                averageActualPrice, actualFeeInQuoteCurrency, tradeIds);
    }
}
