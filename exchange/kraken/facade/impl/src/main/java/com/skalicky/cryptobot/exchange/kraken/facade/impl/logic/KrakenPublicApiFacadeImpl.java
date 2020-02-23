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

package com.skalicky.cryptobot.exchange.kraken.facade.impl.logic;

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import com.skalicky.cryptobot.exchange.kraken.facade.api.bo.KrakenTickerPairBo;
import com.skalicky.cryptobot.exchange.kraken.facade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.kraken.facade.api.logic.KrakenPublicApiFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KrakenPublicApiFacadeImpl implements KrakenPublicApiFacade {
    @Nonnull
    private final KrakenPublicApiConnector krakenPublicApiConnector;
    @Nonnull
    private final NonnullConverter<Map.Entry<String, Map<String, Object>>, KrakenTickerPairBo> mapEntryToKrakenTickerPairBoConverter;

    public KrakenPublicApiFacadeImpl(@Nonnull final KrakenPublicApiConnector krakenPublicApiConnector,
                                     @Nonnull final NonnullConverter<Map.Entry<String, Map<String, Object>>, KrakenTickerPairBo> mapEntryToKrakenTickerPairBoConverter) {
        this.krakenPublicApiConnector = krakenPublicApiConnector;
        this.mapEntryToKrakenTickerPairBoConverter = mapEntryToKrakenTickerPairBoConverter;
    }

    @Override
    @Nonnull
    public List<KrakenTickerPairBo> ticker(@Nonnull final String pairName) {
        return ticker(Collections.singletonList(pairName));
    }

    @Override
    @Nonnull
    public List<KrakenTickerPairBo> ticker(@Nonnull final List<String> pairNames) {
        final KrakenResponseDto<Map<String, Map<String, Object>>> response = krakenPublicApiConnector.ticker(pairNames);

        if (CollectionUtils.isNotEmpty(response.getError())) {
            throw new IllegalStateException(response.getError().toString());
        }
        if (MapUtils.isEmpty(response.getResult())) {
            return Collections.emptyList();
        }

        return response.getResult().entrySet().stream().map(mapEntryToKrakenTickerPairBoConverter::convert).collect(Collectors.toList());
    }
}
