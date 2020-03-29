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

package com.skalicky.cryptobot.exchange.kraken.connector.api.logic;

import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenAddOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public interface KrakenPrivateApiConnector {

    @NotNull
    KrakenResponseDto<KrakenOpenOrderResultDto> openOrders(boolean includeTrades);

    @NotNull
    KrakenResponseDto<KrakenClosedOrderResultDto> closedOrders(boolean includeTrades,
                                                               @NotNull Long fromInEpochSeconds);

    @NotNull
    KrakenResponseDto<Map<String, BigDecimal>> balance();

    @NotNull
    KrakenResponseDto<KrakenAddOrderResultDto> addOrder(@NotNull String krakenMarketName,
                                                        @NotNull String krakenOrderType,
                                                        @NotNull String krakenPriceOrderType,
                                                        @NotNull BigDecimal price,
                                                        @NotNull BigDecimal volumeInQuoteCurrency,
                                                        @NotNull ImmutableList<String> orderFlags,
                                                        long orderExpirationInSecondsFromNow);
}
