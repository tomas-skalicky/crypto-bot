/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2020 $user.name<$user.email>
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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import edu.self.kraken.api.KrakenApi;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KrakenPublicApiConnectorImpl implements KrakenPublicApiConnector {
    @Nonnull
    private final KrakenApi krakenApi;
    @Nonnull
    private final ObjectMapper objectMapper;

    public KrakenPublicApiConnectorImpl(@Nonnull final KrakenApi krakenApi,
                                        @Nonnull final ObjectMapper objectMapper) {
        this.krakenApi = krakenApi;
        this.objectMapper = objectMapper;
    }

    @Override
    @Nonnull
    public KrakenResponseDto<Map<String, Map<String, Object>>> ticker(@Nonnull final List<String> pairNames) {
        if (pairNames.isEmpty()) {
            throw new IllegalArgumentException("pairs is a mandatory argument and cannot be empty");
        }

        final Map<String, String> parameters = Collections.singletonMap("pair", String.join(",", pairNames));
        try {
            final String responseString = krakenApi.queryPublic(KrakenApi.Method.TICKER, parameters);
            return objectMapper.readValue(responseString, new TypeReference<>() {
            });
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
