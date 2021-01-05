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

package com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter;

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDtoBuilder;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDtoBuilder;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.EpochSecondBigDecimalToLocalDateTimeConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.OpenOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

public class KrakenMapEntryToOpenOrderBoConverterUTest {

    @NotNull
    private final KrakenMapEntryToOpenOrderBoConverter converter =
            new KrakenMapEntryToOpenOrderBoConverter(
                    new KrakenOrderTypeToOrderTypeBoEnumConverter(),
                    new KrakenOrderTypeToPriceOrderTypeBoEnumConverter(),
                    new KrakenMarketNameToCurrencyPairBoEnumConverter(),
                    new EpochSecondBigDecimalToLocalDateTimeConverter(),
                    new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter()
            );

    @Test
    public void test_convert_when_allDataProvidedAndValid_then_successfulConversion() {
        // Given
        final var desiredPrice = new BigDecimal("5762.3");
        final KrakenOrderDescriptionDto descriptionDto = KrakenOrderDescriptionDtoBuilder
                .aKrakenOrderDescriptionDto()
                .withType("buy")
                .withOrdertype("limit")
                .withPair("XBTEUR")
                .withPrice(desiredPrice)
                .build();
        final var desiredVolumeInQuoteCurrency = new BigDecimal("1.56");
        final var alreadyExecutedVolumeInQuoteCurrency = new BigDecimal("0.75");
        final var averageActualPrice = new BigDecimal("5762.1");
        final var actualFeeInQuoteCurrency = new BigDecimal("0.005");
        final var tradeId1 = "tradeId1";
        final var tradeId2 = "tradeId2";
        final KrakenOpenOrderDto orderDto = KrakenOpenOrderDtoBuilder.aKrakenOpenOrderDto()
                .withDescr(descriptionDto)
                .withVol(desiredVolumeInQuoteCurrency)
                .withOpentm(BigDecimal.valueOf(1583831100))
                .withStatus("open")
                .withVol_exec(alreadyExecutedVolumeInQuoteCurrency)
                .withPrice(averageActualPrice)
                .withCost(actualFeeInQuoteCurrency)
                .withTrades(List.of(tradeId1, tradeId2))
                .build();
        final var orderId = "orderId123";

        // When
        final OpenOrderBo orderBo = converter.convert(Pair.of(orderId, orderDto));

        // Then
        then(orderBo.getOrderId()).isEqualTo(orderId);
        then(orderBo.getOrderType()).isEqualTo(OrderTypeBoEnum.BUY);
        then(orderBo.getPriceOrderType()).isEqualTo(PriceOrderTypeBoEnum.LIMIT);
        then(orderBo.getCurrencyPair()).isEqualTo(new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR));
        then(orderBo.getDesiredVolumeInQuoteCurrency()).isEqualTo(desiredVolumeInQuoteCurrency);
        then(orderBo.getDesiredPrice()).isEqualTo(desiredPrice);
        then(orderBo.getOpenDateTime()).isEqualTo(LocalDateTime.of(2020, 3, 10, 10, 5));
        then(orderBo.getState()).isEqualTo(OrderStateBoEnum.PARTIALLY_EXECUTED);
        then(orderBo.getAlreadyExecutedVolumeInQuoteCurrency()).isEqualTo(alreadyExecutedVolumeInQuoteCurrency);
        then(orderBo.getAverageActualPrice()).isEqualTo(averageActualPrice);
        then(orderBo.getActualFeeInQuoteCurrency()).isEqualTo(actualFeeInQuoteCurrency);
        then(orderBo.getTradeIds()).containsExactly(tradeId1, tradeId2);
    }

    @Test
    public void test_convert_when_noTradesYet_then_averageActualPriceNotSet_and_actualFeeInQuoteCurrencyNotSet() {
        // Given
        final var desiredPrice = new BigDecimal("5762.3");
        final KrakenOrderDescriptionDto descriptionDto = KrakenOrderDescriptionDtoBuilder
                .aKrakenOrderDescriptionDto()
                .withType("buy")
                .withOrdertype("limit")
                .withPair("XBTEUR")
                .withPrice(desiredPrice)
                .build();
        final var desiredVolumeInQuoteCurrency = new BigDecimal("1.56");
        final var alreadyExecutedVolumeInQuoteCurrency = BigDecimal.ZERO;
        final KrakenOpenOrderDto orderDto = KrakenOpenOrderDtoBuilder.aKrakenOpenOrderDto()
                .withDescr(descriptionDto)
                .withVol(desiredVolumeInQuoteCurrency)
                .withOpentm(BigDecimal.valueOf(1583831100))
                .withStatus("open")
                .withVol_exec(alreadyExecutedVolumeInQuoteCurrency)
                .withPrice(null)
                .withCost(null)
                .withTrades(Collections.emptyList())
                .build();
        final var orderId = "orderId123";

        // When
        final OpenOrderBo orderBo = converter.convert(Pair.of(orderId, orderDto));

        // Then
        then(orderBo.getOrderId()).isEqualTo(orderId);
        then(orderBo.getOrderType()).isEqualTo(OrderTypeBoEnum.BUY);
        then(orderBo.getPriceOrderType()).isEqualTo(PriceOrderTypeBoEnum.LIMIT);
        then(orderBo.getCurrencyPair()).isEqualTo(new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR));
        then(orderBo.getDesiredVolumeInQuoteCurrency()).isEqualTo(desiredVolumeInQuoteCurrency);
        then(orderBo.getDesiredPrice()).isEqualTo(desiredPrice);
        then(orderBo.getOpenDateTime()).isEqualTo(LocalDateTime.of(2020, 3, 10, 10, 5));
        then(orderBo.getState()).isEqualTo(OrderStateBoEnum.NEW);
        then(orderBo.getAlreadyExecutedVolumeInQuoteCurrency()).isEqualTo(alreadyExecutedVolumeInQuoteCurrency);
        then(orderBo.getAverageActualPrice()).isNull();
        then(orderBo.getActualFeeInQuoteCurrency()).isNull();
        then(orderBo.getTradeIds()).isEmpty();
    }
}
