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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connector.api.util.KrakenLocalDateTimeSerializer;
import edu.self.kraken.api.KrakenApi;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class KrakenPrivateApiConnectorImpl implements KrakenPrivateApiConnector {
    @Nonnull
    private final KrakenApi krakenApi;
    @Nonnull
    private final ObjectMapper objectMapper;
    @Nonnull
    private final KrakenLocalDateTimeSerializer krakenLocalDateTimeSerializer;

    public KrakenPrivateApiConnectorImpl(@Nonnull final KrakenApi krakenApi,
                                         @Nonnull final ObjectMapper objectMapper,
                                         @Nonnull final KrakenLocalDateTimeSerializer krakenLocalDateTimeSerializer) {
        this.krakenApi = krakenApi;
        this.objectMapper = objectMapper;
        this.krakenLocalDateTimeSerializer = krakenLocalDateTimeSerializer;
    }

    @Nonnull
    @Override
    public KrakenResponseDto<Map<String, Map<String, Object>>> openOrders(final boolean includeTrades) {
        // TODO Tomas not implemented yet. I am waiting for the first open order in Kraken. OPEN_ORDERS is very likely what I want.
        return null;
    }

    @Nonnull
    @Override
    public KrakenResponseDto<KrakenClosedOrderResultDto> closedOrders(final boolean includeTrades,
                                                                      @Nonnull final LocalDateTime from) {
        final Map<String, String> parameters = Collections.unmodifiableMap(Map.of(
                "trades", String.valueOf(includeTrades),
                "start", String.valueOf(krakenLocalDateTimeSerializer.serialize(from))
        ));
        try {
            final String responseString = krakenApi.queryPrivate(KrakenApi.Method.CLOSED_ORDERS, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Nonnull
    @Override
    public KrakenResponseDto<Map<String, BigDecimal>> balance() {
        try {
            final String responseString = krakenApi.queryPrivate(KrakenApi.Method.BALANCE);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Nonnull
    @Override
    public KrakenResponseDto<KrakenAddOrderResultDto> addOrder(@Nonnull final String krakenMarketName,
                                                               @Nonnull final String krakenOrderType,
                                                               @Nonnull final String krakenPriceOrderType,
                                                               @Nonnull final BigDecimal price,
                                                               @Nonnull final BigDecimal volumeInQuoteCurrency,
                                                               @Nonnull final ImmutableList<String> orderFlags,
                                                               final long orderExpirationInSecondsFromNow) {
        final Map<String, String> parameters = Collections.unmodifiableMap(Map.of(
                "pair", krakenMarketName,
                "type", krakenOrderType,
                "ordertype", krakenPriceOrderType,
                "price", price.toPlainString(),
                "volume", volumeInQuoteCurrency.toPlainString(),
                "oflags", String.join(",", orderFlags),
                "expiretm", "+" + orderExpirationInSecondsFromNow
        ));
        try {
            //FIXME Tomas 4 final
//            if (true) {
//                return new KrakenResponseDto<>();
//            }
            final String responseString = krakenApi.queryPrivate(KrakenApi.Method.ADD_ORDER, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException | InvalidKeyException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }
}
