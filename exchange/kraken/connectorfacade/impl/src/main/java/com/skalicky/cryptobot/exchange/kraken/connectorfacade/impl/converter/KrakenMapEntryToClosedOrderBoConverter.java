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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDto;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class KrakenMapEntryToClosedOrderBoConverter
        implements NonnullConverter<Map.Entry<String, KrakenClosedOrderDto>, ClosedOrderBo> {

    @Nonnull
    private final NonnullConverter<String, OrderTypeBoEnum> krakenOrderTypeToOrderTypeBoEnumConverter;
    @Nonnull
    private final NonnullConverter<String, PriceOrderTypeBoEnum> krakenOrderTypeToPriceOrderTypeBoEnumConverter;
    @Nonnull
    private final NonnullConverter<String, CurrencyPairBo> krakenMarketNameToCurrencyPairBoEnumConverter;
    @Nonnull
    private final NonnullConverter<BigDecimal, LocalDateTime> epochSecondBigDecimalToLocalDateTimeConverter;
    @Nonnull
    private final NonnullConverter<Pair<String, Integer>, OrderStateBoEnum> pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter;

    public KrakenMapEntryToClosedOrderBoConverter(@Nonnull final NonnullConverter<String, OrderTypeBoEnum> krakenOrderTypeToOrderTypeBoEnumConverter,
                                                  @Nonnull final NonnullConverter<String, PriceOrderTypeBoEnum> krakenOrderTypeToPriceOrderTypeBoEnumConverter,
                                                  @Nonnull final NonnullConverter<String, CurrencyPairBo> krakenMarketNameToCurrencyPairBoEnumConverter,
                                                  @Nonnull final NonnullConverter<BigDecimal, LocalDateTime> epochSecondBigDecimalToLocalDateTimeConverter,
                                                  @Nonnull final NonnullConverter<Pair<String, Integer>, OrderStateBoEnum> pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter) {
        this.krakenOrderTypeToOrderTypeBoEnumConverter = krakenOrderTypeToOrderTypeBoEnumConverter;
        this.krakenOrderTypeToPriceOrderTypeBoEnumConverter = krakenOrderTypeToPriceOrderTypeBoEnumConverter;
        this.krakenMarketNameToCurrencyPairBoEnumConverter = krakenMarketNameToCurrencyPairBoEnumConverter;
        this.epochSecondBigDecimalToLocalDateTimeConverter = epochSecondBigDecimalToLocalDateTimeConverter;
        this.pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter = pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter;
    }

    @Override
    @Nonnull
    public ClosedOrderBo convert(@Nonnull final Map.Entry<String, KrakenClosedOrderDto> inputEntry) {
        final KrakenClosedOrderDto inputOrder = inputEntry.getValue();
        final KrakenOrderDescriptionDto inputOrderDescription = inputOrder.getDescr();
        Objects.requireNonNull(inputOrderDescription);
        Objects.requireNonNull(inputOrderDescription.getType());
        Objects.requireNonNull(inputOrderDescription.getOrdertype());
        Objects.requireNonNull(inputOrderDescription.getPair());
        final CurrencyPairBo currencyPair =
                krakenMarketNameToCurrencyPairBoEnumConverter.convert(inputOrderDescription.getPair());
        Objects.requireNonNull(inputOrder.getVol());
        Objects.requireNonNull(inputOrder.getOpentm());
        Objects.requireNonNull(inputOrder.getClosetm());
        Objects.requireNonNull(inputOrder.getStatus());
        Objects.requireNonNull(inputOrder.getVol_exec());
        Objects.requireNonNull(inputOrder.getPrice());
        Objects.requireNonNull(inputOrder.getCost());
        final ImmutableList<String> outputTrades = inputOrder.getTrades() == null
                ? ImmutableList.<String>builder().build() : ImmutableList.copyOf(inputOrder.getTrades());
        final OrderStateBoEnum outputState = pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter.convert(
                Pair.of(inputOrder.getStatus(), outputTrades.size()));

        return new ClosedOrderBo(
                inputEntry.getKey(),
                krakenOrderTypeToOrderTypeBoEnumConverter.convert(inputOrderDescription.getType()),
                krakenOrderTypeToPriceOrderTypeBoEnumConverter.convert(inputOrderDescription.getOrdertype()),
                currencyPair,
                inputOrder.getVol(),
                inputOrderDescription.getPrice(),
                epochSecondBigDecimalToLocalDateTimeConverter.convert(inputOrder.getOpentm()),
                epochSecondBigDecimalToLocalDateTimeConverter.convert(inputOrder.getClosetm()),
                outputState,
                inputOrder.getVol_exec(),
                inputOrder.getPrice(),
                inputOrder.getCost(),
                outputTrades
        );
    }
}
