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

package com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.api.logic.KrakenPrivateApiFacade;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class KrakenPrivateApiFacadeImpl implements KrakenPrivateApiFacade {
    @NotNull
    private final KrakenPrivateApiConnector krakenPrivateApiConnector;
    @NotNull
    private final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter;
    @NotNull
    private final NonnullConverter<OrderTypeBoEnum, String> orderTypeBoEnumToKrakenOrderTypeConverter;
    @NotNull
    private final NonnullConverter<PriceOrderTypeBoEnum, String> priceOrderTypeBoEnumToKrakenOrderTypeConverter;
    @NotNull
    private final NonnullConverter<String, CurrencyBoEnum> krakenCurrencyNameToCurrencyBoEnumConverter;
    @NotNull
    private final NonnullConverter<Map.Entry<String, KrakenOpenOrderDto>, OpenOrderBo> krakenMapEntryToOpenOrderBoConverter;
    @NotNull
    private final NonnullConverter<Map.Entry<String, KrakenClosedOrderDto>, ClosedOrderBo> krakenMapEntryToClosedOrderBoConverter;
    @NotNull
    private final NonnullConverter<LocalDateTime, Long> localDateTimeToEpochSecondLongConverter;

    public KrakenPrivateApiFacadeImpl(@NotNull final KrakenPrivateApiConnector krakenPrivateApiConnector,
                                      @NotNull final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter,
                                      @NotNull final NonnullConverter<OrderTypeBoEnum, String> orderTypeBoEnumToKrakenOrderTypeConverter,
                                      @NotNull final NonnullConverter<PriceOrderTypeBoEnum, String> priceOrderTypeBoEnumToKrakenOrderTypeConverter,
                                      @NotNull final NonnullConverter<String, CurrencyBoEnum> krakenCurrencyNameToCurrencyBoEnumConverter,
                                      @NotNull final NonnullConverter<Map.Entry<String, KrakenOpenOrderDto>, OpenOrderBo> krakenMapEntryToOpenOrderBoConverter,
                                      @NotNull final NonnullConverter<Map.Entry<String, KrakenClosedOrderDto>, ClosedOrderBo> krakenMapEntryToClosedOrderBoConverter,
                                      @NotNull final NonnullConverter<LocalDateTime, Long> localDateTimeToEpochSecondLongConverter) {
        this.krakenPrivateApiConnector = krakenPrivateApiConnector;
        this.currencyPairBoEnumToKrakenMarketNameConverter = currencyPairBoEnumToKrakenMarketNameConverter;
        this.orderTypeBoEnumToKrakenOrderTypeConverter = orderTypeBoEnumToKrakenOrderTypeConverter;
        this.priceOrderTypeBoEnumToKrakenOrderTypeConverter = priceOrderTypeBoEnumToKrakenOrderTypeConverter;
        this.krakenCurrencyNameToCurrencyBoEnumConverter = krakenCurrencyNameToCurrencyBoEnumConverter;
        this.krakenMapEntryToOpenOrderBoConverter = krakenMapEntryToOpenOrderBoConverter;
        this.krakenMapEntryToClosedOrderBoConverter = krakenMapEntryToClosedOrderBoConverter;
        this.localDateTimeToEpochSecondLongConverter = localDateTimeToEpochSecondLongConverter;
    }

    @NotNull
    @Override
    public ImmutableList<OpenOrderBo> getOpenOrders(final boolean includeTrades) {
        final var response = krakenPrivateApiConnector.openOrders(includeTrades);

        if (CollectionUtils.isNotEmpty(response.getError())) {
            throw new IllegalStateException(response.getError().toString());
        }
        if (response.getResult() == null || MapUtils.isEmpty(response.getResult().getOpen())) {
            return ImmutableList.<OpenOrderBo>builder().build();
        }

        return ImmutableList.copyOf(response.getResult().getOpen().entrySet().stream() //
                .map(krakenMapEntryToOpenOrderBoConverter::convert) //
                .collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public ImmutableList<ClosedOrderBo> getClosedOrders(final boolean includeTrades,
                                                        @NotNull final LocalDateTime from) {
        final var fromInEpochSeconds = localDateTimeToEpochSecondLongConverter.convert(from);
        final var response = krakenPrivateApiConnector.closedOrders(includeTrades, fromInEpochSeconds);

        if (CollectionUtils.isNotEmpty(response.getError())) {
            throw new IllegalStateException(response.getError().toString());
        }
        if (response.getResult() == null || MapUtils.isEmpty(response.getResult().getClosed())) {
            return ImmutableList.<ClosedOrderBo>builder().build();
        }

        return ImmutableList.copyOf(response.getResult().getClosed().entrySet().stream() //
                .map(krakenMapEntryToClosedOrderBoConverter::convert) //
                .collect(Collectors.toList()));
    }

    @NotNull
    @Override
    public ImmutableMap<CurrencyBoEnum, BigDecimal> getAccountBalance() {
        final var response = krakenPrivateApiConnector.balance();

        if (CollectionUtils.isNotEmpty(response.getError())) {
            throw new IllegalStateException(response.getError().toString());
        }
        if (MapUtils.isEmpty(response.getResult())) {
            return ImmutableMap.<CurrencyBoEnum, BigDecimal>builder().build();
        }

        return ImmutableMap.copyOf(response.getResult().entrySet().stream() //
                .filter(e -> krakenCurrencyNameToCurrencyBoEnumConverter.convert(e.getKey()) != CurrencyBoEnum.OTHERS) //
                .collect(Collectors.toUnmodifiableMap(
                        e -> krakenCurrencyNameToCurrencyBoEnumConverter.convert(e.getKey()), Map.Entry::getValue)));
    }

    @Override
    public void placeOrder(@NotNull final OrderTypeBoEnum orderType,
                           @NotNull final PriceOrderTypeBoEnum priceOrderType,
                           @NotNull final CurrencyPairBo currencyPair,
                           @NotNull final BigDecimal volumeInQuoteCurrency,
                           @NotNull final BigDecimal price,
                           final boolean preferFeeInQuoteCurrency,
                           final long orderExpirationInSecondsFromNow) {

        final var krakenMarketName = currencyPairBoEnumToKrakenMarketNameConverter.convert(currencyPair);
        final var krakenOrderType = orderTypeBoEnumToKrakenOrderTypeConverter.convert(orderType);
        final var krakenPriceOrderType = priceOrderTypeBoEnumToKrakenOrderTypeConverter.convert(priceOrderType);
        final var krakenOrderFlags = new ArrayList<String>();
        if (preferFeeInQuoteCurrency) {
            krakenOrderFlags.add("fciq");
        }
        final BigDecimal krakenPrice;
        if (currencyPair.getQuoteCurrency() == CurrencyBoEnum.BTC
                && currencyPair.getBaseCurrency() == CurrencyBoEnum.EUR) {
            // Scale set to 1 due to Kraken constraint:
            // "EOrder:Invalid price:XXBTZEUR price can only be specified up to 1 decimals."
            krakenPrice = price.setScale(1, RoundingMode.HALF_UP);
        } else {
            krakenPrice = price;
        }

        final var response = krakenPrivateApiConnector.addOrder(krakenMarketName,
                krakenOrderType, krakenPriceOrderType, krakenPrice, volumeInQuoteCurrency,
                ImmutableList.copyOf(krakenOrderFlags), orderExpirationInSecondsFromNow);

        if (CollectionUtils.isNotEmpty(response.getError())) {
            throw new IllegalStateException(response.getError().toString());
        }
    }
}
