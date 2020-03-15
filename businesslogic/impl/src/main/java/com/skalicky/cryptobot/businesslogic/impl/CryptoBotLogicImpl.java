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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.businesslogic.api.CryptoBotLogic;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.SlackFacade;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CryptoBotLogicImpl implements CryptoBotLogic {

    @Nonnull
    private static final DateTimeFormatter CLOSED_ORDER_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM. HH:mm");
    @Nonnull
    private static final Logger logger = LoggerFactory.getLogger(CryptoBotLogicImpl.class);
    @Nonnull
    private final ImmutableMap<String, TradingPlatformPublicApiFacade> publicApiFacadesByPlatformNames;
    @Nonnull
    private final ImmutableMap<String, TradingPlatformPrivateApiFacade> privateApiFacadesByPlatformNames;
    @Nonnull
    private final SlackFacade slackFacade;
    @Nonnull
    private final LocalDateTimeProvider localDateTimeProvider;

    public CryptoBotLogicImpl(@Nonnull final ImmutableList<TradingPlatformPublicApiFacade> publicApiFacades,
                              @Nonnull final ImmutableList<TradingPlatformPrivateApiFacade> privateApiFacades,
                              @Nonnull final SlackFacade slackFacade,
                              @Nonnull final LocalDateTimeProvider localDateTimeProvider) {
        this.publicApiFacadesByPlatformNames = ImmutableMap.copyOf(publicApiFacades.stream()
                .collect(Collectors.toUnmodifiableMap(
                        TradingPlatformDesignated::getTradingPlatform, Function.identity())));
        this.privateApiFacadesByPlatformNames = ImmutableMap.copyOf(privateApiFacades.stream()
                .collect(Collectors.toUnmodifiableMap(
                        TradingPlatformDesignated::getTradingPlatform, Function.identity())));
        this.slackFacade = slackFacade;
        this.localDateTimeProvider = localDateTimeProvider;
    }

    @Override
    public void reportOpenOrders(@Nonnull final String tradingPlatformName,
                                 @Nullable final String slackWebhookUrl) {
        final TradingPlatformPrivateApiFacade facade = privateApiFacadesByPlatformNames.get(tradingPlatformName);
        if (facade == null) {
            throw new IllegalArgumentException("No private API facade for the trading platform \""
                    + tradingPlatformName + "\"");
        }

        // TODO Tomas not implemented yet. I am waiting for the first open order in Kraken.
    }

    @Override
    public void reportClosedOrders(@Nonnull final String tradingPlatformName,
                                   @Nullable final String slackWebhookUrl) {
        final TradingPlatformPrivateApiFacade facade = privateApiFacadesByPlatformNames.get(tradingPlatformName);
        if (facade == null) {
            throw new IllegalArgumentException("No private API facade for the trading platform \""
                    + tradingPlatformName + "\"");
        }

        final LocalDateTime from = localDateTimeProvider.now().minusDays(3);
        final List<ClosedOrderBo> closedOrdersWithTrades = facade.getClosedOrders(true, from).stream() //
                .filter(o -> !o.getTradeIds().isEmpty()) //
                .collect(Collectors.toList());
        final StringBuilder messageBuilder = new StringBuilder("Closed orders since ")
                .append(from.format(CLOSED_ORDER_DATE_TIME_FORMATTER)).append(" on ")
                .append(tradingPlatformName).append(": ");
        if (closedOrdersWithTrades.isEmpty()) {
            messageBuilder.append("none");
        } else {
            closedOrdersWithTrades.forEach(o -> messageBuilder.append(System.lineSeparator())
                    .append(toStringForNotificationPurposes(o)));
        }
        final String message = messageBuilder.toString();
        logger.info(message);
        if (slackWebhookUrl != null) {
            slackFacade.sendMessage(message, slackWebhookUrl);
        }
    }

    private String toStringForNotificationPurposes(@Nonnull final ClosedOrderBo order) {
        final CurrencyPairBo currencyPair = order.getCurrencyPair();
        final CurrencyBoEnum quoteCurrency = currencyPair.getQuoteCurrency();
        final BigDecimal desiredPrice = order.getDesiredPrice();
        final String tradesString = order.getTradeIds().size() > 1 ? "trades" : "trade";
        final String desiredPriceString = desiredPrice != null ? desiredPrice + " " : "";
        return order.getOrderType().getLabel() + " "
                + order.getDesiredVolumeInQuoteCurrency() + " "
                + quoteCurrency.getLabel() + "-" + currencyPair.getBaseCurrency().getLabel() + " exec. "
                + order.getTotalExecutedVolumeInQuoteCurrency() + " " + quoteCurrency.getLabel() + " @ "
                + order.getPriceOrderType().getLabel() + " "
                + desiredPriceString + "exec. avg. "
                + order.getAverageActualPrice() + " fee "
                + order.getActualFeeInQuoteCurrency() + " " + quoteCurrency.getLabel() + " @ "
                + order.getStatus().getLabel() + " @ open "
                + order.getOpenDateTime().format(CLOSED_ORDER_DATE_TIME_FORMATTER) + " close "
                + order.getCloseDateTime().format(CLOSED_ORDER_DATE_TIME_FORMATTER) + " @ "
                + order.getTradeIds().size() + " " + tradesString;
    }

    @Override
    public void placeBuyOrderIfEnoughAvailable(@Nonnull final String tradingPlatformName,
                                               @Nonnull final BigDecimal volumeInBaseCurrencyToInvestPerRun,
                                               @Nonnull final String baseCurrencyLabel,
                                               @Nonnull final String quoteCurrencyLabel,
                                               @Nonnull final BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal,
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
            final CurrencyPairBo currencyPair = new CurrencyPairBo(quoteCurrency, baseCurrency);
            final TickerBo ticker = publicApiFacade.getTicker(currencyPair);

            final BigDecimal price = ticker.getBidPrice().multiply(BigDecimal.ONE.subtract(offsetRatioOfLimitPriceToBidPriceInDecimal));
            final BigDecimal volumeInQuoteCurrency = volumeInBaseCurrencyToInvestPerRun.divide(price, 10,
                    RoundingMode.HALF_UP);
            final OrderTypeBoEnum orderType = OrderTypeBoEnum.BUY;
            final PriceOrderTypeBoEnum priceOrderType = PriceOrderTypeBoEnum.LIMIT;
            // 36 hours
            final long orderExpirationInSecondsFromNow = 60 * 60 * 36;

            final String orderMessage = "Going to place a " + priceOrderType.getLabel() + " order to "
                    + orderType.getLabel() + " " + volumeInQuoteCurrency + " " + quoteCurrencyLabel + " for "
                    + volumeInBaseCurrencyToInvestPerRun + " " + baseCurrencyLabel + " on " + tradingPlatformName
                    + ". Limit price of 1 " + quoteCurrencyLabel + " = " + price + " " + baseCurrencyLabel
                    + ". Order expiration is in " + orderExpirationInSecondsFromNow + " seconds from now.";
            logger.info(orderMessage);

            privateApiFacade.placeOrder(orderType, priceOrderType, currencyPair,
                    volumeInQuoteCurrency, price, true, orderExpirationInSecondsFromNow);

            final String orderPlacedMessage = priceOrderType.getLabel() + " order to "
                    + orderType.getLabel() + " " + volumeInQuoteCurrency + " " + quoteCurrencyLabel + " for "
                    + volumeInBaseCurrencyToInvestPerRun + " " + baseCurrencyLabel
                    + " successfully placed on " + tradingPlatformName
                    + ". Limit price of 1 " + quoteCurrencyLabel + " = " + price + " " + baseCurrencyLabel
                    + ". Order expiration is in " + orderExpirationInSecondsFromNow + " seconds from now.";
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
