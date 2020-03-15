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

public final class OpenOrderBoBuilder {
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
    @Nullable
    private LocalDateTime expirationDateTime;
    @Nonnull
    private OrderStateBoEnum status = OrderStateBoEnum.NEW;
    @Nonnull
    private BigDecimal alreadyExecutedVolumeInQuoteCurrency = BigDecimal.ZERO;
    @Nullable
    private BigDecimal averageActualPrice;
    @Nullable
    private BigDecimal actualFeeInQuoteCurrency;
    @Nonnull
    private ImmutableList<String> tradeIds = ImmutableList.<String>builder().build();

    private OpenOrderBoBuilder() {
    }

    @Nonnull
    public static OpenOrderBoBuilder aOpenOrderBo() {
        return new OpenOrderBoBuilder();
    }

    @Nonnull
    public OpenOrderBoBuilder withOrderId(@Nonnull final String orderId) {
        this.orderId = orderId;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withOrderType(@Nonnull final OrderTypeBoEnum orderType) {
        this.orderType = orderType;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withPriceOrderType(@Nonnull final PriceOrderTypeBoEnum priceOrderType) {
        this.priceOrderType = priceOrderType;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withCurrencyPair(@Nonnull final CurrencyPairBo currencyPair) {
        this.currencyPair = currencyPair;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withDesiredVolumeInQuoteCurrency(@Nonnull final BigDecimal desiredVolumeInQuoteCurrency) {
        this.desiredVolumeInQuoteCurrency = desiredVolumeInQuoteCurrency;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withDesiredPrice(@Nullable final BigDecimal desiredPrice) {
        this.desiredPrice = desiredPrice;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withOpenDateTime(@Nonnull final LocalDateTime openDateTime) {
        this.openDateTime = openDateTime;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withExpirationDateTime(@Nonnull final LocalDateTime expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withStatus(@Nonnull final OrderStateBoEnum status) {
        this.status = status;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withAlreadyExecutedVolumeInQuoteCurrency(@Nonnull final BigDecimal alreadyExecutedVolumeInQuoteCurrency) {
        this.alreadyExecutedVolumeInQuoteCurrency = alreadyExecutedVolumeInQuoteCurrency;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withAverageActualPrice(@Nullable final BigDecimal averageActualPrice) {
        this.averageActualPrice = averageActualPrice;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withActualFeeInQuoteCurrency(@Nullable final BigDecimal actualFeeInQuoteCurrency) {
        this.actualFeeInQuoteCurrency = actualFeeInQuoteCurrency;
        return this;
    }

    @Nonnull
    public OpenOrderBoBuilder withTradeIds(@Nonnull final ImmutableList<String> tradeIds) {
        this.tradeIds = tradeIds;
        return this;
    }

    @Nonnull
    public OpenOrderBo build() {
        return new OpenOrderBo(orderId, orderType, priceOrderType, currencyPair, desiredVolumeInQuoteCurrency,
                desiredPrice, openDateTime, expirationDateTime, status, alreadyExecutedVolumeInQuoteCurrency,
                averageActualPrice, actualFeeInQuoteCurrency, tradeIds);
    }
}
