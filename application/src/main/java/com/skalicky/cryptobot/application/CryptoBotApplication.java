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

package com.skalicky.cryptobot.application;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.businesslogic.api.CryptoBotLogic;
import com.skalicky.cryptobot.businesslogic.impl.CryptoBotLogicImpl;
import com.skalicky.cryptobot.businesslogic.impl.LocalDateTimeProviderImpl;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connector.impl.logic.KrakenPrivateApiConnectorImpl;
import com.skalicky.cryptobot.exchange.kraken.connector.impl.logic.KrakenPublicApiConnectorImpl;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.api.logic.KrakenPrivateApiFacade;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.api.logic.KrakenPublicApiFacade;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.CurrencyPairBoToKrakenMarketNameConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenCurrencyNameToCurrencyBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToClosedOrderBoConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToOpenOrderBoConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToTickerBoConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMarketNameToCurrencyPairBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToPriceOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.OrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PriceOrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic.KrakenPrivateApiFacadeImpl;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic.KrakenPublicApiFacadeImpl;
import com.skalicky.cryptobot.exchange.shared.connector.impl.logic.RestConnectorSupport;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.EpochSecondBigDecimalToLocalDateTimeConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.LocalDateTimeToEpochSecondLongConverter;
import com.skalicky.cryptobot.exchange.slack.connector.api.SlackConnector;
import com.skalicky.cryptobot.exchange.slack.connector.impl.SlackConnectorImpl;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.SlackFacade;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.impl.SlackFacadeImpl;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPublicApiFacade;
import edu.self.kraken.api.KrakenApi;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CryptoBotApplication {

    @Nonnull
    private static final String KRAKEN_TRADING_PLATFORM_NAME = "kraken";

    public static void main(String[] args) {
        final CryptoBotArguments arguments = new CryptoBotArguments();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter =
                new CurrencyPairBoToKrakenMarketNameConverter();
        final List<TradingPlatformPublicApiFacade> publicApiFacades = new ArrayList<>();
        final List<TradingPlatformPrivateApiFacade> privateApiFacades = new ArrayList<>();
        if (KRAKEN_TRADING_PLATFORM_NAME.equals(arguments.getTradingPlatformName())) {
            final KrakenApi krakenApi = initializeKrakenApi(arguments);
            final ObjectMapper objectMapper = new ObjectMapper();
            publicApiFacades.add(initializeKrakenPublicApiFacade(krakenApi, objectMapper, currencyPairBoEnumToKrakenMarketNameConverter));
            privateApiFacades.add(initializeKrakenPrivateApiFacade(krakenApi, objectMapper, currencyPairBoEnumToKrakenMarketNameConverter));
        }
        final SlackFacade slackFacade = initializeSlackFacade();
        final CryptoBotLogic cryptoBotLogic = new CryptoBotLogicImpl(ImmutableList.copyOf(publicApiFacades),
                ImmutableList.copyOf(privateApiFacades), slackFacade, new LocalDateTimeProviderImpl());

        try {
//            cryptoBotLogic.placeBuyOrderIfEnoughAvailable(arguments.getTradingPlatformName(),
//                    arguments.getVolumeInBaseCurrencyToInvestPerRun(),
//                    arguments.getBaseCurrency(),
//                    arguments.getQuoteCurrency(),
//                    arguments.getOffsetRatioOfLimitPriceToBidPriceInDecimal(),
//                    arguments.getSlackWebhookUrl());
            cryptoBotLogic.reportClosedOrders(arguments.getTradingPlatformName(), arguments.getSlackWebhookUrl());
            cryptoBotLogic.reportOpenOrders(arguments.getTradingPlatformName(), arguments.getSlackWebhookUrl());

        } catch (Exception ex) {
            ex.printStackTrace();
            if (arguments.getSlackWebhookUrl() != null) {
                final String stackTrace = ExceptionUtils.getStackTrace(ex);
                final int maxLength = 512;
                final boolean showDots = stackTrace.length() > maxLength;
                slackFacade.sendMessage("Exception: "
                                + stackTrace.substring(0, Math.min(maxLength, stackTrace.length()))
                                + (showDots ? "..." : ""),
                        arguments.getSlackWebhookUrl());
            }
        }
    }

    @Nonnull
    private static KrakenApi initializeKrakenApi(@Nonnull final CryptoBotArguments arguments) {
        final KrakenApi krakenApi = new KrakenApi();
        krakenApi.setKey(arguments.getTradingPlatformKey());
        krakenApi.setSecret(arguments.getTradingPlatformSecret());
        return krakenApi;
    }

    @Nonnull
    private static KrakenPrivateApiFacade initializeKrakenPrivateApiFacade(@Nonnull final KrakenApi krakenApi,
                                                                           @Nonnull final ObjectMapper objectMapper,
                                                                           @Nonnull final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter) {
        final KrakenPrivateApiConnector krakenPrivateApiConnector = new KrakenPrivateApiConnectorImpl(krakenApi,
                objectMapper);
        final NonnullConverter<OrderTypeBoEnum, String> orderTypeBoEnumToKrakenOrderTypeConverter =
                new OrderTypeBoEnumToKrakenOrderTypeConverter();
        final NonnullConverter<PriceOrderTypeBoEnum, String> priceOrderTypeBoEnumToKrakenOrderTypeConverter =
                new PriceOrderTypeBoEnumToKrakenOrderTypeConverter();
        final NonnullConverter<String, CurrencyBoEnum> krakenCurrencyNameToCurrencyBoEnumConverter =
                new KrakenCurrencyNameToCurrencyBoEnumConverter();
        final NonnullConverter<String, OrderTypeBoEnum> krakenOrderTypeToOrderTypeBoEnumConverter =
                new KrakenOrderTypeToOrderTypeBoEnumConverter();
        final NonnullConverter<String, PriceOrderTypeBoEnum> krakenOrderTypeToPriceOrderTypeBoEnumConverter =
                new KrakenOrderTypeToPriceOrderTypeBoEnumConverter();
        final NonnullConverter<String, CurrencyPairBo> krakenMarketNameToCurrencyPairBoEnumConverter =
                new KrakenMarketNameToCurrencyPairBoEnumConverter();
        final NonnullConverter<BigDecimal, LocalDateTime> epochSecondBigDecimalToLocalDateTimeConverter =
                new EpochSecondBigDecimalToLocalDateTimeConverter();
        final NonnullConverter<Pair<String, Integer>, OrderStateBoEnum> pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter =
                new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter();
        final NonnullConverter<Map.Entry<String, KrakenOpenOrderDto>, OpenOrderBo> krakenMapEntryToOpenOrderBoConverter =
                new KrakenMapEntryToOpenOrderBoConverter(
                        krakenOrderTypeToOrderTypeBoEnumConverter,
                        krakenOrderTypeToPriceOrderTypeBoEnumConverter,
                        krakenMarketNameToCurrencyPairBoEnumConverter,
                        epochSecondBigDecimalToLocalDateTimeConverter,
                        pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter
                );
        final NonnullConverter<Map.Entry<String, KrakenClosedOrderDto>, ClosedOrderBo> krakenMapEntryToClosedOrderBoConverter =
                new KrakenMapEntryToClosedOrderBoConverter(
                        krakenOrderTypeToOrderTypeBoEnumConverter,
                        krakenOrderTypeToPriceOrderTypeBoEnumConverter,
                        krakenMarketNameToCurrencyPairBoEnumConverter,
                        epochSecondBigDecimalToLocalDateTimeConverter,
                        pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter
                );
        return new KrakenPrivateApiFacadeImpl(
                krakenPrivateApiConnector,
                currencyPairBoEnumToKrakenMarketNameConverter,
                orderTypeBoEnumToKrakenOrderTypeConverter,
                priceOrderTypeBoEnumToKrakenOrderTypeConverter,
                krakenCurrencyNameToCurrencyBoEnumConverter,
                krakenMapEntryToOpenOrderBoConverter,
                krakenMapEntryToClosedOrderBoConverter,
                new LocalDateTimeToEpochSecondLongConverter());
    }

    @Nonnull
    private static KrakenPublicApiFacade initializeKrakenPublicApiFacade(@Nonnull final KrakenApi krakenApi,
                                                                         @Nonnull final ObjectMapper objectMapper,
                                                                         @Nonnull final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter) {
        final KrakenPublicApiConnector krakenPublicApiConnector = new KrakenPublicApiConnectorImpl(krakenApi,
                objectMapper);
        final NonnullConverter<Map.Entry<String, Map<String, Object>>, TickerBo> krakenMapEntryToTickerBoConverter =
                new KrakenMapEntryToTickerBoConverter();
        return new KrakenPublicApiFacadeImpl(krakenPublicApiConnector,
                currencyPairBoEnumToKrakenMarketNameConverter,
                krakenMapEntryToTickerBoConverter);
    }

    @Nonnull
    private static SlackFacade initializeSlackFacade() {
        final RestConnectorSupport restConnectorSupport = new RestConnectorSupport();
        final SlackConnector slackConnector = new SlackConnectorImpl(restConnectorSupport);
        return new SlackFacadeImpl(slackConnector);
    }
}
