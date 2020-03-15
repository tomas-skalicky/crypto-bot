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

package com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter;

import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDto;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class KrakenMapEntryToOpenOrderBoConverter
        implements NonnullConverter<Map.Entry<String, KrakenOpenOrderDto>, OpenOrderBo> {

    @Nonnull
    private final NonnullConverter<String, OrderTypeBoEnum> krakenOrderTypeToOrderTypeBoEnumConverter;
    @Nonnull
    private final NonnullConverter<String, PriceOrderTypeBoEnum> krakenOrderTypeToPriceOrderTypeBoEnumConverter;
    @Nonnull
    private final NonnullConverter<String, CurrencyPairBo> krakenMarketNameToCurrencyPairBoEnumConverter;
    @Nonnull
    private final NonnullConverter<BigDecimal, LocalDateTime> epochSecondBigDecimalToLocalDateTimeConverter;
    @Nonnull
    private final NonnullConverter<String, OrderStateBoEnum> krakenOrderStatusToOrderStateBoEnumConverter;

    public KrakenMapEntryToOpenOrderBoConverter(@Nonnull final NonnullConverter<String, OrderTypeBoEnum> krakenOrderTypeToOrderTypeBoEnumConverter,
                                                @Nonnull final NonnullConverter<String, PriceOrderTypeBoEnum> krakenOrderTypeToPriceOrderTypeBoEnumConverter,
                                                @Nonnull final NonnullConverter<String, CurrencyPairBo> krakenMarketNameToCurrencyPairBoEnumConverter,
                                                @Nonnull final NonnullConverter<BigDecimal, LocalDateTime> epochSecondBigDecimalToLocalDateTimeConverter,
                                                @Nonnull final NonnullConverter<String, OrderStateBoEnum> krakenOrderStatusToOrderStateBoEnumConverter) {
        this.krakenOrderTypeToOrderTypeBoEnumConverter = krakenOrderTypeToOrderTypeBoEnumConverter;
        this.krakenOrderTypeToPriceOrderTypeBoEnumConverter = krakenOrderTypeToPriceOrderTypeBoEnumConverter;
        this.krakenMarketNameToCurrencyPairBoEnumConverter = krakenMarketNameToCurrencyPairBoEnumConverter;
        this.epochSecondBigDecimalToLocalDateTimeConverter = epochSecondBigDecimalToLocalDateTimeConverter;
        this.krakenOrderStatusToOrderStateBoEnumConverter = krakenOrderStatusToOrderStateBoEnumConverter;
    }

    @Override
    @Nonnull
    public OpenOrderBo convert(@Nonnull final Map.Entry<String, KrakenOpenOrderDto> inputEntry) {
        final KrakenOpenOrderDto inputOrder = inputEntry.getValue();
        final KrakenOrderDescriptionDto inputOrderDescription = inputOrder.getDescr();
        Objects.requireNonNull(inputOrderDescription);
        Objects.requireNonNull(inputOrderDescription.getType());
        Objects.requireNonNull(inputOrderDescription.getOrdertype());
        Objects.requireNonNull(inputOrderDescription.getPair());
        final CurrencyPairBo currencyPair =
                krakenMarketNameToCurrencyPairBoEnumConverter.convert(inputOrderDescription.getPair());
        Objects.requireNonNull(inputOrder.getVol());
        Objects.requireNonNull(inputOrder.getOpentm());
        Objects.requireNonNull(inputOrder.getStatus());
        Objects.requireNonNull(inputOrder.getVol_exec());
        final ImmutableList<String> outputTrades = inputOrder.getTrades() == null
                ? ImmutableList.<String>builder().build() : ImmutableList.copyOf(inputOrder.getTrades());

        return new OpenOrderBo(
                inputEntry.getKey(),
                krakenOrderTypeToOrderTypeBoEnumConverter.convert(inputOrderDescription.getType()),
                krakenOrderTypeToPriceOrderTypeBoEnumConverter.convert(inputOrderDescription.getOrdertype()),
                currencyPair,
                inputOrder.getVol(),
                inputOrderDescription.getPrice(),
                epochSecondBigDecimalToLocalDateTimeConverter.convert(inputOrder.getOpentm()),
                krakenOrderStatusToOrderStateBoEnumConverter.convert(inputOrder.getStatus()),
                inputOrder.getVol_exec(),
                inputOrder.getPrice(),
                inputOrder.getCost(),
                outputTrades
        );
    }
}
