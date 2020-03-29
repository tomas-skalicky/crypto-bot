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

package com.skalicky.cryptobot.exchange.kraken.connector.impl.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenAddOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import edu.self.kraken.api.KrakenApi;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

public class KrakenPrivateApiConnectorImpl implements KrakenPrivateApiConnector {
    @NotNull
    private final KrakenApi krakenApi;
    @NotNull
    private final ObjectMapper objectMapper;

    public KrakenPrivateApiConnectorImpl(@NotNull final KrakenApi krakenApi,
                                         @NotNull final ObjectMapper objectMapper) {
        this.krakenApi = krakenApi;
        this.objectMapper = objectMapper;
    }

    @NotNull
    @Override
    public KrakenResponseDto<KrakenOpenOrderResultDto> openOrders(final boolean includeTrades) {
        final var parameters = Collections.singletonMap(
                "trades", String.valueOf(includeTrades));
        try {
            final var responseString = krakenApi.queryPrivate(KrakenApi.Method.OPEN_ORDERS, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    @NotNull
    @Override
    public KrakenResponseDto<KrakenClosedOrderResultDto> closedOrders(final boolean includeTrades,
                                                                      @NotNull final Long fromInEpochSeconds) {
        final var parameters = Collections.unmodifiableMap(Map.of(
                "trades", String.valueOf(includeTrades),
                "start", String.valueOf(fromInEpochSeconds)));
        try {
            final var responseString = krakenApi.queryPrivate(KrakenApi.Method.CLOSED_ORDERS, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    @NotNull
    @Override
    public KrakenResponseDto<Map<String, BigDecimal>> balance() {
        try {
            final var responseString = krakenApi.queryPrivate(KrakenApi.Method.BALANCE);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    @NotNull
    @Override
    public KrakenResponseDto<KrakenAddOrderResultDto> addOrder(@NotNull final String krakenMarketName,
                                                               @NotNull final String krakenOrderType,
                                                               @NotNull final String krakenPriceOrderType,
                                                               @NotNull final BigDecimal price,
                                                               @NotNull final BigDecimal volumeInQuoteCurrency,
                                                               @NotNull final ImmutableList<String> orderFlags,
                                                               final long orderExpirationInSecondsFromNow) {
        final var parameters = Collections.unmodifiableMap(Map.of(
                "pair", krakenMarketName,
                "type", krakenOrderType,
                "ordertype", krakenPriceOrderType,
                "price", price.toPlainString(),
                "volume", volumeInQuoteCurrency.toPlainString(),
                "oflags", String.join(",", orderFlags),
                "expiretm", "+" + orderExpirationInSecondsFromNow
        ));
        try {
            final var responseString = krakenApi.queryPrivate(KrakenApi.Method.ADD_ORDER, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }
}
