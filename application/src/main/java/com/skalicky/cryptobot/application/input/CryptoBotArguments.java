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

package com.skalicky.cryptobot.application.input;

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

    @Nullable
    @Parameter(names = {"--slackWebhookUrl"},
            description = "Slack Webhook to notify the user about placing of orders, open and closed orders, etc.")
    private String slackWebhookUrl;

    @NotNull
    public String getBaseCurrency() {
        return baseCurrency;
    }

    @NotNull
    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    @NotNull
    public BigDecimal getVolumeInBaseCurrencyToInvestPerRun() {
        return volumeInBaseCurrencyToInvestPerRun;
    }

    @NotNull
    public String getTradingPlatformName() {
        return tradingPlatformName;
    }

    @NotNull
    public String getTradingPlatformKey() {
        return tradingPlatformKey;
    }

    @NotNull
    public String getTradingPlatformSecret() {
        return tradingPlatformSecret;
    }

    @NotNull
    public BigDecimal getOffsetRatioOfLimitPriceToBidPriceInDecimal() {
        return offsetRatioOfLimitPriceToBidPriceInDecimal;
    }

    @Nullable
    public String getSlackWebhookUrl() {
        return slackWebhookUrl;
    }
}
