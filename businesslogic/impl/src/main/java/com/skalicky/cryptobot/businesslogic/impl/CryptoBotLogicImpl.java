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

package com.skalicky.cryptobot.businesslogic.impl;

import com.skalicky.cryptobot.businesslogic.api.CryptoBotLogic;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.SlackFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformDesignated;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPrivateApiFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.logic.TradingPlatformPublicApiFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CryptoBotLogicImpl implements CryptoBotLogic {

    @Nonnull
    private static final BigDecimal NINETY_NINE_PERCENT_IN_DECIMAL = new BigDecimal("0.99");
    @Nonnull
    private static final Logger logger = LoggerFactory.getLogger(CryptoBotLogicImpl.class);
    @Nonnull
    private final Map<String, TradingPlatformPublicApiFacade> publicApiFacadesByPlatformNames;
    @Nonnull
    private final Map<String, TradingPlatformPrivateApiFacade> privateApiFacadesByPlatformNames;
    @Nonnull
    private final SlackFacade slackFacade;

    public CryptoBotLogicImpl(@Nonnull final List<TradingPlatformPublicApiFacade> publicApiFacades,
                              @Nonnull final List<TradingPlatformPrivateApiFacade> privateApiFacades,
                              @Nonnull final SlackFacade slackFacade) {
        this.publicApiFacadesByPlatformNames = publicApiFacades.stream()
                .collect(Collectors.toMap(TradingPlatformDesignated::getTradingPlatform, Function.identity()));
        this.privateApiFacadesByPlatformNames = privateApiFacades.stream()
                .collect(Collectors.toMap(TradingPlatformDesignated::getTradingPlatform, Function.identity()));
        this.slackFacade = slackFacade;
    }

    @Override
    public void reportOpenOrders(@Nonnull final String tradingPlatformName) {
        final TradingPlatformPrivateApiFacade facade = privateApiFacadesByPlatformNames.get(tradingPlatformName);
        if (facade == null) {
            throw new IllegalArgumentException("No private API facade for the trading platform " + tradingPlatformName);
        }

        // TODO Tomas not implemented yet. I am waiting for the first open order in Kraken.
    }

    @Override
    public void reportClosedOrders(@Nonnull final String tradingPlatformName) {
        final TradingPlatformPrivateApiFacade facade = privateApiFacadesByPlatformNames.get(tradingPlatformName);
        if (facade == null) {
            throw new IllegalArgumentException("No private API facade for the trading platform " + tradingPlatformName);
        }

        // TODO Tomas not implemented yet. I am waiting for the first closed order in Kraken.
    }

    @Override
    public void placeBuyOrderIfEnoughAvailable(@Nonnull final String tradingPlatformName,
                                               @Nonnull final BigDecimal volumeInBaseCurrencyToInvestPerRun,
                                               @Nonnull final String baseCurrencyLabel,
                                               @Nonnull final String quoteCurrencyLabel,
                                               @Nullable final String slackWebhookUrl) {
        final TradingPlatformPrivateApiFacade privateApiFacade = privateApiFacadesByPlatformNames.get(tradingPlatformName);
        if (privateApiFacade == null) {
            throw new IllegalArgumentException("No private API facade for the trading platform \""
                    + tradingPlatformName + "\"");
        }
        final TradingPlatformPublicApiFacade publicApiFacade = publicApiFacadesByPlatformNames.get(tradingPlatformName);
        if (publicApiFacade == null) {
            throw new IllegalArgumentException("No public API facade for the trading platform \""
                    + tradingPlatformName + "\"");
        }

        final CurrencyBoEnum baseCurrency = CurrencyBoEnum.getByLabel(baseCurrencyLabel);
        final BigDecimal baseCurrencyAmount = privateApiFacade.getAccountBalance().get(baseCurrency);
        if (baseCurrencyAmount.compareTo(volumeInBaseCurrencyToInvestPerRun) >= 0) {
            final String tickerMessage = "Going to retrieve a ticker for currencies quote " + quoteCurrencyLabel
                    + " and base " + baseCurrencyLabel + " on " + tradingPlatformName + ".";
            logger.info(tickerMessage);
            if (slackWebhookUrl != null) {
                slackFacade.sendMessage(tickerMessage, slackWebhookUrl);
            }

            final CurrencyBoEnum quoteCurrency = CurrencyBoEnum.getByLabel(quoteCurrencyLabel);
            final TickerBo ticker = publicApiFacade.getTicker(quoteCurrency, baseCurrency);

            final BigDecimal price = ticker.getBidPrice().multiply(NINETY_NINE_PERCENT_IN_DECIMAL);
            final OrderTypeBoEnum orderType = OrderTypeBoEnum.BUY;
            final PriceOrderTypeBoEnum priceOrderType = PriceOrderTypeBoEnum.LIMIT;

            final String orderMessage = "Going to place a " + priceOrderType.getLabel() + " order to "
                    + orderType.getLabel() + " " + quoteCurrencyLabel + " for "
                    + volumeInBaseCurrencyToInvestPerRun + " " + baseCurrencyLabel + " on " + tradingPlatformName
                    + ". Limit price of 1 " + quoteCurrencyLabel + " = " + price + " " + baseCurrencyLabel + ".";
            logger.info(orderMessage);

            privateApiFacade.placeOrder(orderType, priceOrderType, baseCurrency, quoteCurrency,
                    volumeInBaseCurrencyToInvestPerRun, price, true);

            final String orderPlacedMessage = priceOrderType.getLabel() + " order to" + " " + orderType.getLabel()
                    + " " + quoteCurrencyLabel
                    + " for " + volumeInBaseCurrencyToInvestPerRun + " " + baseCurrencyLabel
                    + " successfully placed on " + tradingPlatformName
                    + ". Limit price of 1 " + quoteCurrencyLabel + " = " + price + " " + baseCurrencyLabel;
            logger.info(orderPlacedMessage);
            if (slackWebhookUrl != null) {
                slackFacade.sendMessage(orderPlacedMessage, slackWebhookUrl);
            }

        } else {
            final String message = "Too little base currency [" + baseCurrencyAmount + " "
                    + baseCurrencyLabel + "]. Needed volume to invest per run is "
                    + volumeInBaseCurrencyToInvestPerRun + " " + baseCurrencyLabel;
            logger.warn(message);
            if (slackWebhookUrl != null) {
                slackFacade.sendMessage(message, slackWebhookUrl);
            }
        }
    }
}
