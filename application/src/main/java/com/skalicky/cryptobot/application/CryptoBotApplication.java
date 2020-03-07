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
import com.skalicky.cryptobot.businesslogic.api.CryptoBotLogic;
import com.skalicky.cryptobot.businesslogic.impl.CryptoBotLogicImpl;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connector.impl.logic.KrakenPrivateApiConnectorImpl;
import com.skalicky.cryptobot.exchange.kraken.connector.impl.logic.KrakenPublicApiConnectorImpl;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.api.logic.KrakenPrivateApiFacade;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.api.logic.KrakenPublicApiFacade;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenCurrencyNameToCurrencyBoEnumConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter.KrakenMapEntryToTickerBoConverter;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic.KrakenPrivateApiFacadeImpl;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic.KrakenPublicApiFacadeImpl;
import com.skalicky.cryptobot.exchange.shared.connector.impl.logic.RestConnectorSupport;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.slack.connector.api.SlackConnector;
import com.skalicky.cryptobot.exchange.slack.connector.impl.SlackConnectorImpl;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.SlackFacade;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.impl.SlackFacadeImpl;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPublicApiFacade;
import edu.self.kraken.api.KrakenApi;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Nonnull;
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

        final List<TradingPlatformPublicApiFacade> publicApiFacades = new ArrayList<>();
        final List<TradingPlatformPrivateApiFacade> privateApiFacades = new ArrayList<>();
        if (KRAKEN_TRADING_PLATFORM_NAME.equals(arguments.getTradingPlatformName())) {
            final KrakenApi krakenApi = initializeKrakenApi(arguments);
            final ObjectMapper objectMapper = new ObjectMapper();
            publicApiFacades.add(initializeKrakenPublicApiFacade(krakenApi, objectMapper));
            privateApiFacades.add(initializeKrakenPrivateApiFacade(krakenApi, objectMapper));
        }
        final SlackFacade slackFacade = initializeSlackFacade();
        final CryptoBotLogic cryptoBotLogic = new CryptoBotLogicImpl(publicApiFacades, privateApiFacades, slackFacade);

        try {
            cryptoBotLogic.reportOpenOrders(arguments.getTradingPlatformName());
            cryptoBotLogic.reportClosedOrders(arguments.getTradingPlatformName());
            cryptoBotLogic.placeBuyOrderIfEnoughAvailable(arguments.getTradingPlatformName(),
                    arguments.getVolumeInBaseCurrencyToInvestPerRun(),
                    arguments.getBaseCurrency(),
                    arguments.getSlackWebhookUrl());

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
                                                                           @Nonnull final ObjectMapper objectMapper) {
        final KrakenPrivateApiConnector krakenPrivateApiConnector = new KrakenPrivateApiConnectorImpl(krakenApi,
                objectMapper);
        final NonnullConverter<String, CurrencyBoEnum> krakenCurrencyNameToCurrencyBoEnumConverter =
                new KrakenCurrencyNameToCurrencyBoEnumConverter();
        return new KrakenPrivateApiFacadeImpl(
                krakenPrivateApiConnector, krakenCurrencyNameToCurrencyBoEnumConverter);
    }

    @Nonnull
    private static KrakenPublicApiFacade initializeKrakenPublicApiFacade(@Nonnull final KrakenApi krakenApi,
                                                                         @Nonnull final ObjectMapper objectMapper) {
        final KrakenPublicApiConnector krakenPublicApiConnector = new KrakenPublicApiConnectorImpl(krakenApi,
                objectMapper);
        final NonnullConverter<Map.Entry<String, Map<String, Object>>, TickerBo> krakenMapEntryToTickerBoConverter =
                new KrakenMapEntryToTickerBoConverter();
        return new KrakenPublicApiFacadeImpl(krakenPublicApiConnector,
                krakenMapEntryToTickerBoConverter);
    }

    @Nonnull
    private static SlackFacade initializeSlackFacade() {
        final RestConnectorSupport restConnectorSupport = new RestConnectorSupport();
        final SlackConnector slackConnector = new SlackConnectorImpl(restConnectorSupport);
        return new SlackFacadeImpl(slackConnector);
    }
}
