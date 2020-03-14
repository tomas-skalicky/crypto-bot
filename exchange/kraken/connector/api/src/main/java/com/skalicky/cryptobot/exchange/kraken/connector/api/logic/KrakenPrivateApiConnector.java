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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface KrakenPrivateApiConnector {

    @Nonnull
    KrakenResponseDto<Map<String, Map<String, Object>>> openOrders(boolean includeTrades);

    @Nonnull
    KrakenResponseDto<KrakenClosedOrderResultDto> closedOrders(boolean includeTrades,
                                                               @Nonnull LocalDateTime from);

    @Nonnull
    KrakenResponseDto<Map<String, BigDecimal>> balance();

    @Nonnull
    KrakenResponseDto<KrakenAddOrderResultDto> addOrder(@Nonnull String krakenMarketName,
                                                        @Nonnull String krakenOrderType,
                                                        @Nonnull String krakenPriceOrderType,
                                                        @Nonnull BigDecimal price,
                                                        @Nonnull BigDecimal volumeInQuoteCurrency,
                                                        @Nonnull ImmutableList<String> orderFlags,
                                                        long orderExpirationInSecondsFromNow);
}
