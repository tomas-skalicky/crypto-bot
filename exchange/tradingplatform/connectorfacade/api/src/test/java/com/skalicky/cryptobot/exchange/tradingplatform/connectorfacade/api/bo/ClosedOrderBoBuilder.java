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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ClosedOrderBoBuilder {
    @Nonnull
    private String orderId = "";
    @Nonnull
    private OrderTypeBoEnum orderType = OrderTypeBoEnum.BUY;
    @Nonnull
    private PriceOrderTypeBoEnum priceOrderType = PriceOrderTypeBoEnum.LIMIT;
    @Nonnull
    private CurrencyPairBo currencyPair = new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR);
    @Nonnull
    private BigDecimal desiredVolumeInQuoteCurrency = new BigDecimal("0.56");
    @Nullable
    private BigDecimal desiredPrice = BigDecimal.valueOf(4988);
    @Nonnull
    private LocalDateTime openDateTime = LocalDateTime.of(2020, 3, 13, 21, 0);
    @Nonnull
    private LocalDateTime closeDateTime = LocalDateTime.of(2020, 3, 14, 10, 0);
    @Nonnull
    private OrderStateBoEnum status = OrderStateBoEnum.FULLY_EXECUTED;
    @Nonnull
    private BigDecimal totalExecutedVolumeInQuoteCurrency = new BigDecimal("0.56");
    @Nonnull
    private BigDecimal averageActualPrice = BigDecimal.valueOf(4985);
    @Nonnull
    private BigDecimal actualFeeInQuoteCurrency = BigDecimal.ZERO;
    @Nonnull
    private ImmutableList<String> tradeIds = ImmutableList.<String>builder().build();

    private ClosedOrderBoBuilder() {
    }

    @Nonnull
    public static ClosedOrderBoBuilder aClosedOrderBo() {
        return new ClosedOrderBoBuilder();
    }

    @Nonnull
    public ClosedOrderBoBuilder withOrderId(@Nonnull final String orderId) {
        this.orderId = orderId;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withOrderType(@Nonnull final OrderTypeBoEnum orderType) {
        this.orderType = orderType;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withPriceOrderType(@Nonnull final PriceOrderTypeBoEnum priceOrderType) {
        this.priceOrderType = priceOrderType;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withCurrencyPair(@Nonnull final CurrencyPairBo currencyPair) {
        this.currencyPair = currencyPair;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withDesiredVolumeInQuoteCurrency(@Nonnull final BigDecimal desiredVolumeInQuoteCurrency) {
        this.desiredVolumeInQuoteCurrency = desiredVolumeInQuoteCurrency;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withDesiredPrice(@Nullable final BigDecimal desiredPrice) {
        this.desiredPrice = desiredPrice;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withOpenDateTime(@Nonnull final LocalDateTime openDateTime) {
        this.openDateTime = openDateTime;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withCloseDateTime(@Nonnull final LocalDateTime closeDateTime) {
        this.closeDateTime = closeDateTime;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withStatus(@Nonnull final OrderStateBoEnum status) {
        this.status = status;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withTotalExecutedVolumeInQuoteCurrency(@Nonnull final BigDecimal totalExecutedVolumeInQuoteCurrency) {
        this.totalExecutedVolumeInQuoteCurrency = totalExecutedVolumeInQuoteCurrency;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withAverageActualPrice(@Nonnull final BigDecimal averageActualPrice) {
        this.averageActualPrice = averageActualPrice;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withActualFeeInQuoteCurrency(@Nonnull final BigDecimal actualFeeInQuoteCurrency) {
        this.actualFeeInQuoteCurrency = actualFeeInQuoteCurrency;
        return this;
    }

    @Nonnull
    public ClosedOrderBoBuilder withTradeIds(@Nonnull final ImmutableList<String> tradeIds) {
        this.tradeIds = tradeIds;
        return this;
    }

    @Nonnull
    public ClosedOrderBo build() {
        return new ClosedOrderBo(orderId, orderType, priceOrderType, currencyPair, desiredVolumeInQuoteCurrency,
                desiredPrice, openDateTime, closeDateTime, status, totalExecutedVolumeInQuoteCurrency,
                averageActualPrice, actualFeeInQuoteCurrency, tradeIds);
    }
}
