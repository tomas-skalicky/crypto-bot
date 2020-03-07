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

package com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic;

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPrivateApiConnector;
import com.skalicky.cryptobot.exchange.kraken.connectorfacade.api.logic.KrakenPrivateApiFacade;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KrakenPrivateApiFacadeImpl implements KrakenPrivateApiFacade {
    @Nonnull
    private final KrakenPrivateApiConnector krakenPrivateApiConnector;
    @Nonnull
    private final NonnullConverter<String, CurrencyBoEnum> krakenCurrencyNameToCurrencyBoEnumConverter;

    public KrakenPrivateApiFacadeImpl(@Nonnull final KrakenPrivateApiConnector krakenPrivateApiConnector,
                                      @Nonnull final NonnullConverter<String, CurrencyBoEnum> krakenCurrencyNameToCurrencyBoEnumConverter) {
        this.krakenPrivateApiConnector = krakenPrivateApiConnector;
        this.krakenCurrencyNameToCurrencyBoEnumConverter = krakenCurrencyNameToCurrencyBoEnumConverter;
    }

    @Nonnull
    @Override
    public List<OpenOrderBo> getOpenOrders(final boolean includeTrades) {
        // TODO Tomas not implemented yet. I am waiting for the first open order in Kraken.
        return null;
    }

    @Nonnull
    @Override
    public List<ClosedOrderBo> getClosedOrders(final boolean includeTrades,
                                               @Nonnull final LocalDateTime from) {
        // TODO Tomas not implemented yet. I am waiting for the first closed order in Kraken.
        return null;
    }

    @Nonnull
    @Override
    public Map<CurrencyBoEnum, BigDecimal> getAccountBalance() {
        final KrakenResponseDto<Map<String, BigDecimal>> response = krakenPrivateApiConnector.getBalance();

        if (CollectionUtils.isNotEmpty(response.getError())) {
            throw new IllegalStateException(response.getError().toString());
        }
        if (MapUtils.isEmpty(response.getResult())) {
            return new HashMap<>();
        }

        return response.getResult().entrySet().stream() //
                .filter(e -> krakenCurrencyNameToCurrencyBoEnumConverter.convert(e.getKey()) != CurrencyBoEnum.OTHERS)
                .collect(Collectors.toMap(e -> krakenCurrencyNameToCurrencyBoEnumConverter.convert(e.getKey()), Map.Entry::getValue));
    }
}
