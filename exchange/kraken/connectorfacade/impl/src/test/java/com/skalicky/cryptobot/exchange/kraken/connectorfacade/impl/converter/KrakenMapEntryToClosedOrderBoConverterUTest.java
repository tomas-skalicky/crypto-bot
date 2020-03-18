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

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDtoBuilder;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDtoBuilder;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter.EpochSecondBigDecimalToLocalDateTimeConverter;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.ClosedOrderBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.CurrencyPairBo;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.CurrencyBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderTypeBoEnum;
import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.PriceOrderTypeBoEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class KrakenMapEntryToClosedOrderBoConverterUTest {

    @Nonnull
    private final KrakenMapEntryToClosedOrderBoConverter converter =
            new KrakenMapEntryToClosedOrderBoConverter(
                    new KrakenOrderTypeToOrderTypeBoEnumConverter(),
                    new KrakenOrderTypeToPriceOrderTypeBoEnumConverter(),
                    new KrakenMarketNameToCurrencyPairBoEnumConverter(),
                    new EpochSecondBigDecimalToLocalDateTimeConverter(),
                    new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter()
            );

    @Test
    public void test_convert_when_allDataProvidedAndValid_then_successfulConversion() {
        final var desiredPrice = new BigDecimal("5762.3");
        final var descriptionDto = KrakenOrderDescriptionDtoBuilder
                .aKrakenOrderDescriptionDto()
                .withType("buy")
                .withOrdertype("limit")
                .withPair("XBTEUR")
                .withPrice(desiredPrice)
                .build();
        final var desiredVolumeInQuoteCurrency = new BigDecimal("1.56");
        final var executedVolumeInQuoteCurrency = new BigDecimal("1.55");
        final var averageActualPrice = new BigDecimal("5762.1");
        final var actualFeeInQuoteCurrency = new BigDecimal("0.01");
        final var tradeId1 = "tradeId1";
        final var tradeId2 = "tradeId2";
        final var orderDto = KrakenClosedOrderDtoBuilder.aKrakenClosedOrderDto()
                .withDescr(descriptionDto)
                .withVol(desiredVolumeInQuoteCurrency)
                .withOpentm(BigDecimal.valueOf(1583831100))
                .withClosetm(BigDecimal.valueOf(1583831200))
                .withStatus("closed")
                .withVol_exec(executedVolumeInQuoteCurrency)
                .withPrice(averageActualPrice)
                .withCost(actualFeeInQuoteCurrency)
                .withTrades(List.of(tradeId1, tradeId2))
                .build();
        final var orderId = "orderId123";

        final var orderBo = converter.convert(Pair.of(orderId, orderDto));

        assertThat(orderBo.getOrderId()).isEqualTo(orderId);
        assertThat(orderBo.getOrderType()).isEqualTo(OrderTypeBoEnum.BUY);
        assertThat(orderBo.getPriceOrderType()).isEqualTo(PriceOrderTypeBoEnum.LIMIT);
        assertThat(orderBo.getCurrencyPair()).isEqualTo(new CurrencyPairBo(CurrencyBoEnum.BTC, CurrencyBoEnum.EUR));
        assertThat(orderBo.getDesiredVolumeInQuoteCurrency()).isEqualTo(desiredVolumeInQuoteCurrency);
        assertThat(orderBo.getDesiredPrice()).isEqualTo(desiredPrice);
        assertThat(orderBo.getOpenDateTime()).isEqualTo(LocalDateTime.of(2020, 3, 10, 10, 5));
        assertThat(orderBo.getCloseDateTime()).isEqualTo(LocalDateTime.of(2020, 3, 10, 10, 6, 40));
        assertThat(orderBo.getStatus()).isEqualTo(OrderStateBoEnum.FULLY_EXECUTED);
        assertThat(orderBo.getTotalExecutedVolumeInQuoteCurrency()).isEqualTo(executedVolumeInQuoteCurrency);
        assertThat(orderBo.getAverageActualPrice()).isEqualTo(averageActualPrice);
        assertThat(orderBo.getActualFeeInQuoteCurrency()).isEqualTo(actualFeeInQuoteCurrency);
        assertThat(orderBo.getTradeIds()).containsExactly(tradeId1, tradeId2);
    }
}
