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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skalicky.cryptobot.businesslogic.api.CryptoBotLogic;
import com.skalicky.cryptobot.businesslogic.impl.CryptoBotLogicBean;
import com.skalicky.cryptobot.exchange.kraken.connector.impl.logic.KrakenPublicApiConnectorBean;
import com.skalicky.cryptobot.exchange.kraken.facade.api.logic.KrakenPublicApiFacade;
import com.skalicky.cryptobot.exchange.kraken.facade.impl.converter.KrakenMapEntryToTickerBoConverter;
import com.skalicky.cryptobot.exchange.kraken.facade.impl.logic.KrakenPublicApiFacadeBean;
import com.skalicky.cryptobot.exchange.shared.facade.api.bo.TickerBo;
import com.skalicky.cryptobot.exchange.shared.facade.api.converter.NonnullConverter;
import edu.self.kraken.api.KrakenApi;

import java.util.List;
import java.util.Map;

public class CryptoBotApplication {

    public static void main(String[] args) {
        final KrakenApi krakenApi = new KrakenApi();
        final ObjectMapper objectMapper = new ObjectMapper();
        final KrakenPublicApiConnectorBean krakenPublicApiConnector = new KrakenPublicApiConnectorBean(krakenApi, objectMapper);
        final NonnullConverter<Map.Entry<String, Map<String, Object>>, TickerBo> krakenMapEntryToTickerBoConverter = new KrakenMapEntryToTickerBoConverter();
        final KrakenPublicApiFacade krakenPublicApiFacade = new KrakenPublicApiFacadeBean(krakenPublicApiConnector, krakenMapEntryToTickerBoConverter);
        final CryptoBotLogic cryptoBotLogic = new CryptoBotLogicBean(List.of(krakenPublicApiFacade));
        cryptoBotLogic.placeBuyOrder("kraken");
    }
}
