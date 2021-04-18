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

import com.beust.jcommander.Parameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class CryptoBotArguments {

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--baseCurrency"},
            description = "Currency to buy for",
            required = true)
    private String baseCurrency = "";

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--quoteCurrency"},
            description = "Currency to buy",
            required = true)
    private String quoteCurrency = "";

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--volumeInBaseCurrencyToInvestPerRun"},
            description = "How much of the base currency will be intended to be invested into the market currency per run of this bot",
            required = true)
    private BigDecimal volumeInBaseCurrencyToInvestPerRun = BigDecimal.ZERO;

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--tradingPlatformName"},
            description = "Name of trading platform. Currently supported: kraken",
            required = true)
    private String tradingPlatformName = "";

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--tradingPlatformApiKey"},
            description = "Key for private part of trading platform API",
            required = true)
    private String tradingPlatformKey = "";

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--tradingPlatformApiSecret"},
            description = "Secret for private part of trading platform API",
            required = true)
    private String tradingPlatformSecret = "";

    /**
     * Initialized to avoid nullability.
     */
    @NotNull
    @Parameter(names = {"--offsetRatioOfLimitPriceToBidPriceInDecimal"},
            description = "Offset ratio of limit price to the bid price. In Decimal. Sample value: 0.01 (= limit" +
                    " price 1% below the bid price)",
            required = true)
    private BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal = BigDecimal.ZERO;

    /**
     * Initialized to avoid nullability.
     */
    @Parameter(names = {"--minOffsetFromOpenDateTimeOfLastBuyOrderInHours"},
            description = "Minimal offset from the open date-time of last BUY order. The offset is an integer of hours. Default value is 24.")
    private int minOffsetFromOpenDateTimeOfLastBuyOrderInHours = 24;

    @Nullable
    @Parameter(names = {"--slackWebhookUrl"},
            description = "Slack Webhook to notify the user about placing of orders, open and closed orders, etc.")
    private String slackWebhookUrl;

    @NotNull
    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(@NotNull final String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    @NotNull
    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(@NotNull final String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    @NotNull
    public BigDecimal getVolumeInBaseCurrencyToInvestPerRun() {
        return volumeInBaseCurrencyToInvestPerRun;
    }

    public void setVolumeInBaseCurrencyToInvestPerRun(@NotNull final BigDecimal volumeInBaseCurrencyToInvestPerRun) {
        this.volumeInBaseCurrencyToInvestPerRun = volumeInBaseCurrencyToInvestPerRun;
    }

    @NotNull
    public String getTradingPlatformName() {
        return tradingPlatformName;
    }

    public void setTradingPlatformName(@NotNull final String tradingPlatformName) {
        this.tradingPlatformName = tradingPlatformName;
    }

    @NotNull
    public String getTradingPlatformKey() {
        return tradingPlatformKey;
    }

    public void setTradingPlatformKey(@NotNull final String tradingPlatformKey) {
        this.tradingPlatformKey = tradingPlatformKey;
    }

    @NotNull
    public String getTradingPlatformSecret() {
        return tradingPlatformSecret;
    }

    public void setTradingPlatformSecret(@NotNull final String tradingPlatformSecret) {
        this.tradingPlatformSecret = tradingPlatformSecret;
    }

    @NotNull
    public BigDecimal getOffsetRatioOfLimitPriceToBidPriceInDecimal() {
        return offsetRatioOfLimitPriceToBidPriceInDecimal;
    }

    public void setOffsetRatioOfLimitPriceToBidPriceInDecimal(@NotNull final BigDecimal offsetRatioOfLimitPriceToBidPriceInDecimal) {
        this.offsetRatioOfLimitPriceToBidPriceInDecimal = offsetRatioOfLimitPriceToBidPriceInDecimal;
    }

    public int getMinOffsetFromOpenDateTimeOfLastBuyOrderInHours() {
        return minOffsetFromOpenDateTimeOfLastBuyOrderInHours;
    }

    public void setMinOffsetFromOpenDateTimeOfLastBuyOrderInHours(final int minOffsetFromOpenDateTimeOfLastBuyOrderInHours) {
        this.minOffsetFromOpenDateTimeOfLastBuyOrderInHours = minOffsetFromOpenDateTimeOfLastBuyOrderInHours;
    }

    @Nullable
    public String getSlackWebhookUrl() {
        return slackWebhookUrl;
    }

    public void setSlackWebhookUrl(@Nullable final String slackWebhookUrl) {
        this.slackWebhookUrl = slackWebhookUrl;
    }
}
