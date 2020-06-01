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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.application.input.CryptoBotArguments;
import com.skalicky.cryptobot.businesslogic.impl.CryptoBotLogicImpl;
import com.skalicky.cryptobot.businesslogic.impl.datetime.LocalDateTimeProviderImpl;
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
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenOrderTypeToPriceOrderTypeBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.OrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.PriceOrderTypeBoEnumToKrakenOrderTypeConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic.KrakenPrivateApiFacadeImpl;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic.KrakenPublicApiFacadeImpl;
import com.skalicky.cryptobot.exchange.shared.connector.impl.logic.RestConnectorSupport;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.EpochSecondBigDecimalToLocalDateTimeConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.LocalDateTimeToEpochSecondLongConverter;
import com.skalicky.cryptobot.exchange.slack.connector.impl.logic.SlackConnectorImpl;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.logic.SlackFacade;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.impl.logic.SlackFacadeImpl;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPublicApiFacade;
import edu.self.kraken.api.KrakenApi;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CryptoBotApplication {

    @NotNull
    private static final String KRAKEN_TRADING_PLATFORM_NAME = "kraken";

    public static void main(String[] args) {
        final var arguments = new CryptoBotArguments();
        JCommander.newBuilder()
                .addObject(arguments)
                // FIXME Tomas Check the plugin java9-modularity in a more detail
                // There is probably a bug in a plugin https://github.com/java9-modularity/gradle-modules-plugin
                // which causes that system properties are located after
                //   --module com.skalicky.cryptobot.application/com.skalicky.cryptobot.application.CryptoBotApplication
                // not before the "module" parameter. We do not want to parse the system properties.
                // All currently skipped system properties and other properties:
                //   -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant
                //   com.skalicky.cryptobot.application/com.skalicky.cryptobot.application.CryptoBotApplication
                .acceptUnknownOptions(true)
                .build()
                .parse(args);

        final var currencyPairBoEnumToKrakenMarketNameConverter = new CurrencyPairBoToKrakenMarketNameConverter();
        final var publicApiFacades = new ArrayList<TradingPlatformPublicApiFacade>();
        final var privateApiFacades = new ArrayList<TradingPlatformPrivateApiFacade>();
        final var objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        final String tradingPlatformName = arguments.getTradingPlatformName();
        if (KRAKEN_TRADING_PLATFORM_NAME.equals(tradingPlatformName)) {
            final KrakenApi krakenApi = initializeKrakenApi(arguments);
            publicApiFacades.add(initializeKrakenPublicApiFacade(krakenApi, objectMapper,
                    currencyPairBoEnumToKrakenMarketNameConverter));
            privateApiFacades.add(initializeKrakenPrivateApiFacade(krakenApi, objectMapper,
                    currencyPairBoEnumToKrakenMarketNameConverter));
        } else {
            throw new IllegalArgumentException("Unsupported trading platform [" + tradingPlatformName + "]");
        }
        final RestConnectorSupport restConnectorSupport = new RestConnectorSupport(objectMapper);
        final SlackFacade slackFacade = initializeSlackFacade(arguments.getSlackWebhookUrl(), restConnectorSupport);
        final var cryptoBotLogic = new CryptoBotLogicImpl(ImmutableList.copyOf(publicApiFacades),
                ImmutableList.copyOf(privateApiFacades), slackFacade, new LocalDateTimeProviderImpl());

        try {
            cryptoBotLogic.placeBuyOrderIfEnoughAvailable(tradingPlatformName,
                    arguments.getVolumeInBaseCurrencyToInvestPerRun(),
                    arguments.getBaseCurrency(),
                    arguments.getQuoteCurrency(),
                    arguments.getOffsetRatioOfLimitPriceToBidPriceInDecimal());
            cryptoBotLogic.reportClosedOrders(tradingPlatformName);
            cryptoBotLogic.reportOpenOrders(tradingPlatformName);

        } catch (Exception ex) {
            ex.printStackTrace();
            if (arguments.getSlackWebhookUrl() != null) {
                final String stackTrace = ExceptionUtils.getStackTrace(ex);
                final var maxLength = 512;
                final boolean showDots = stackTrace.length() > maxLength;
                slackFacade.sendMessage("Exception: "
                        + stackTrace.substring(0, Math.min(maxLength, stackTrace.length()))
                        + (showDots ? "..." : ""));
            }
        }
    }

    @NotNull
    private static KrakenApi initializeKrakenApi(@NotNull final CryptoBotArguments arguments) {
        final var krakenApi = new KrakenApi();
        krakenApi.setKey(arguments.getTradingPlatformKey());
        krakenApi.setSecret(arguments.getTradingPlatformSecret());
        return krakenApi;
    }

    @NotNull
    private static KrakenPrivateApiFacade initializeKrakenPrivateApiFacade(@NotNull final KrakenApi krakenApi,
                                                                           @NotNull final ObjectMapper objectMapper,
                                                                           @NotNull final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter) {
        final var krakenPrivateApiConnector = new KrakenPrivateApiConnectorImpl(krakenApi, objectMapper);
        final var orderTypeBoEnumToKrakenOrderTypeConverter = new OrderTypeBoEnumToKrakenOrderTypeConverter();
        final var priceOrderTypeBoEnumToKrakenOrderTypeConverter = new PriceOrderTypeBoEnumToKrakenOrderTypeConverter();
        final var krakenCurrencyNameToCurrencyBoEnumConverter = new KrakenCurrencyNameToCurrencyBoEnumConverter();
        final var krakenOrderTypeToOrderTypeBoEnumConverter = new KrakenOrderTypeToOrderTypeBoEnumConverter();
        final var krakenOrderTypeToPriceOrderTypeBoEnumConverter = new KrakenOrderTypeToPriceOrderTypeBoEnumConverter();
        final var krakenMarketNameToCurrencyPairBoEnumConverter = new KrakenMarketNameToCurrencyPairBoEnumConverter();
        final var epochSecondBigDecimalToLocalDateTimeConverter = new EpochSecondBigDecimalToLocalDateTimeConverter();
        final var pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter =
                new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter();
        final var krakenMapEntryToOpenOrderBoConverter = new KrakenMapEntryToOpenOrderBoConverter(
                krakenOrderTypeToOrderTypeBoEnumConverter,
                krakenOrderTypeToPriceOrderTypeBoEnumConverter,
                krakenMarketNameToCurrencyPairBoEnumConverter,
                epochSecondBigDecimalToLocalDateTimeConverter,
                pairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter
        );
        final var krakenMapEntryToClosedOrderBoConverter = new KrakenMapEntryToClosedOrderBoConverter(
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

    @NotNull
    private static KrakenPublicApiFacade initializeKrakenPublicApiFacade(@NotNull final KrakenApi krakenApi,
                                                                         @NotNull final ObjectMapper objectMapper,
                                                                         @NotNull final NonnullConverter<CurrencyPairBo, String> currencyPairBoEnumToKrakenMarketNameConverter) {
        final var krakenPublicApiConnector = new KrakenPublicApiConnectorImpl(krakenApi, objectMapper);
        final var krakenMapEntryToTickerBoConverter = new KrakenMapEntryToTickerBoConverter();
        return new KrakenPublicApiFacadeImpl(krakenPublicApiConnector, currencyPairBoEnumToKrakenMarketNameConverter,
                krakenMapEntryToTickerBoConverter);
    }

    @NotNull
    private static SlackFacade initializeSlackFacade(@Nullable final String slackWebhookUrl,
                                                     @NotNull final RestConnectorSupport restConnectorSupport) {
        final var slackConnector = new SlackConnectorImpl(restConnectorSupport, slackWebhookUrl);
        return new SlackFacadeImpl(slackConnector);
    }
}
