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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDescriptionDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.util.KrakenLocalDateTimeDeserializer;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;

import javax.annotation.Nonnull;
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
    private final KrakenLocalDateTimeDeserializer krakenLocalDateTimeDeserializer;
    @Nonnull
    private final NonnullConverter<String, OrderStateBoEnum> krakenOrderStatusToOrderStateBoEnumConverter;

    public KrakenMapEntryToClosedOrderBoConverter(@Nonnull final NonnullConverter<String, OrderTypeBoEnum> krakenOrderTypeToOrderTypeBoEnumConverter,
                                                  @Nonnull final NonnullConverter<String, PriceOrderTypeBoEnum> krakenOrderTypeToPriceOrderTypeBoEnumConverter,
                                                  @Nonnull final NonnullConverter<String, CurrencyPairBo> krakenMarketNameToCurrencyPairBoEnumConverter,
                                                  @Nonnull final KrakenLocalDateTimeDeserializer krakenLocalDateTimeDeserializer,
                                                  @Nonnull final NonnullConverter<String, OrderStateBoEnum> krakenOrderStatusToOrderStateBoEnumConverter) {
        this.krakenOrderTypeToOrderTypeBoEnumConverter = krakenOrderTypeToOrderTypeBoEnumConverter;
        this.krakenOrderTypeToPriceOrderTypeBoEnumConverter = krakenOrderTypeToPriceOrderTypeBoEnumConverter;
        this.krakenMarketNameToCurrencyPairBoEnumConverter = krakenMarketNameToCurrencyPairBoEnumConverter;
        this.krakenLocalDateTimeDeserializer = krakenLocalDateTimeDeserializer;
        this.krakenOrderStatusToOrderStateBoEnumConverter = krakenOrderStatusToOrderStateBoEnumConverter;
    }

    @Override
    @Nonnull
    public ClosedOrderBo convert(@Nonnull final Map.Entry<String, KrakenClosedOrderDto> inputEntry) {
        final KrakenClosedOrderDto inputOrder = inputEntry.getValue();
        final KrakenClosedOrderDescriptionDto inputOrderDescription = inputOrder.getDescr();
        Objects.requireNonNull(inputOrderDescription);
        Objects.requireNonNull(inputOrderDescription.getType());
        Objects.requireNonNull(inputOrderDescription.getOrdertype());
        Objects.requireNonNull(inputOrderDescription.getPair());
        final CurrencyPairBo currencyPair =
                krakenMarketNameToCurrencyPairBoEnumConverter.convert(inputOrderDescription.getPair());
        Objects.requireNonNull(inputOrder.getVol());
        Objects.requireNonNull(inputOrderDescription.getPrice());
        Objects.requireNonNull(inputOrder.getOpentm());
        Objects.requireNonNull(inputOrder.getClosetm());
        Objects.requireNonNull(inputOrder.getStatus());
        Objects.requireNonNull(inputOrder.getVol_exec());
        Objects.requireNonNull(inputOrder.getPrice());
        Objects.requireNonNull(inputOrder.getCost());
        final ImmutableList<String> outputTrades = inputOrder.getTrades() == null
                ? ImmutableList.<String>builder().build() : ImmutableList.copyOf(inputOrder.getTrades());

        return new ClosedOrderBo(
                inputEntry.getKey(),
                krakenOrderTypeToOrderTypeBoEnumConverter.convert(inputOrderDescription.getType()),
                krakenOrderTypeToPriceOrderTypeBoEnumConverter.convert(inputOrderDescription.getOrdertype()),
                currencyPair,
                inputOrder.getVol(),
                inputOrderDescription.getPrice(),
                krakenLocalDateTimeDeserializer.deserialize(inputOrder.getOpentm()),
                krakenLocalDateTimeDeserializer.deserialize(inputOrder.getClosetm()),
                krakenOrderStatusToOrderStateBoEnumConverter.convert(inputOrder.getStatus()),
                inputOrder.getVol_exec(),
                inputOrder.getPrice(),
                inputOrder.getCost(),
                outputTrades
        );
    }
}
