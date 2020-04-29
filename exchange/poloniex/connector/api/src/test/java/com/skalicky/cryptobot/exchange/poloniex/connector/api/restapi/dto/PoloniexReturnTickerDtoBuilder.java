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

package com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.dto;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;

public final class PoloniexReturnTickerDtoBuilder {
    @Nullable
    private Integer id;
    @Nullable
    private BigDecimal last;
    @Nullable
    private BigDecimal lowestAsk;
    @Nullable
    private BigDecimal highestBid;
    @Nullable
    private BigDecimal percentChange;
    @Nullable
    private BigDecimal baseVolume;
    @Nullable
    private BigDecimal quoteVolume;
    @Nullable
    private Boolean isFrozen;
    @Nullable
    private BigDecimal high24hr;
    @Nullable
    private BigDecimal low24hr;

    private PoloniexReturnTickerDtoBuilder() {
    }

    @Nonnull
    public static PoloniexReturnTickerDtoBuilder aPoloniexReturnTickerDto() {
        return new PoloniexReturnTickerDtoBuilder();
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withId(@Nullable final Integer id) {
        this.id = id;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withLast(@Nullable final BigDecimal last) {
        this.last = last;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withLowestAsk(@Nullable final BigDecimal lowestAsk) {
        this.lowestAsk = lowestAsk;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withHighestBid(@Nullable final BigDecimal highestBid) {
        this.highestBid = highestBid;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withPercentChange(@Nullable final BigDecimal percentChange) {
        this.percentChange = percentChange;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withBaseVolume(@Nullable final BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withQuoteVolume(@Nullable final BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withFrozen(@Nullable final Boolean isFrozen) {
        this.isFrozen = isFrozen;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withHigh24hr(@Nullable final BigDecimal high24hr) {
        this.high24hr = high24hr;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDtoBuilder withLow24hr(@Nullable final BigDecimal low24hr) {
        this.low24hr = low24hr;
        return this;
    }

    @Nonnull
    public PoloniexReturnTickerDto build() {
        final PoloniexReturnTickerDto poloniexReturnTickerDto = new PoloniexReturnTickerDto();
        poloniexReturnTickerDto.setId(id);
        poloniexReturnTickerDto.setLast(last);
        poloniexReturnTickerDto.setLowestAsk(lowestAsk);
        poloniexReturnTickerDto.setHighestBid(highestBid);
        poloniexReturnTickerDto.setPercentChange(percentChange);
        poloniexReturnTickerDto.setBaseVolume(baseVolume);
        poloniexReturnTickerDto.setQuoteVolume(quoteVolume);
        poloniexReturnTickerDto.setFrozen(isFrozen);
        poloniexReturnTickerDto.setHigh24hr(high24hr);
        poloniexReturnTickerDto.setLow24hr(low24hr);
        return poloniexReturnTickerDto;
    }
}
