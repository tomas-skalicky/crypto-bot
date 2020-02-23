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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import edu.self.kraken.api.KrakenApi;

import javax.annotation.Nonnull;

public class KrakenPrivateApiConnectorImpl implements KrakenPrivateApiConnector {
    @Nonnull
    private final KrakenApi krakenApi;
    @Nonnull
    private final ObjectMapper objectMapper;

    KrakenPrivateApiConnectorImpl(@Nonnull final KrakenApi krakenApi,
                                  @Nonnull final ObjectMapper objectMapper) {
        this.krakenApi = krakenApi;
        this.objectMapper = objectMapper;
    }
}
