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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import edu.self.kraken.api.KrakenApi;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class KrakenPublicApiConnectorImpl implements KrakenPublicApiConnector {
    @NotNull
    private final KrakenApi krakenApi;
    @NotNull
    private final ObjectMapper objectMapper;

    public KrakenPublicApiConnectorImpl(@NotNull final KrakenApi krakenApi,
                                        @NotNull final ObjectMapper objectMapper) {
        this.krakenApi = krakenApi;
        this.objectMapper = objectMapper;
    }

    @Override
    @NotNull
    public KrakenResponseDto<Map<String, Map<String, Object>>> ticker(@NotNull final ImmutableList<String> marketNames) {
        if (marketNames.isEmpty()) {
            throw new IllegalArgumentException("Market names are mandatory and at least one name needs to be provided");
        }

        final var parameters = Collections.singletonMap("pair", String.join(",", marketNames));
        try {
            final var responseString = krakenApi.queryPublic(KrakenApi.Method.TICKER, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
