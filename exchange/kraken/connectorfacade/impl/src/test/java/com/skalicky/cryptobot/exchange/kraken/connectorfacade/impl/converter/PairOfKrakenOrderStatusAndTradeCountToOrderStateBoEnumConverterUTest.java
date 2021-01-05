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

import com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums.OrderStateBoEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

public class PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverterUTest {
    @NotNull
    private final PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter converter =
            new PairOfKrakenOrderStatusAndTradeCountToOrderStateBoEnumConverter();

    @Test
    public void test_convert_when_open_and_noTrades_then_stateNew() {
        // When
        final OrderStateBoEnum actual = converter.convert(Pair.of("open", 0));

        // Then
        then(actual).isEqualTo(OrderStateBoEnum.NEW);
    }

    @Test
    public void test_convert_when_open_and_tradesExist_then_statePartiallyExecuted() {
        // When
        final OrderStateBoEnum actual = converter.convert(Pair.of("open", 3));

        // Then
        then(actual).isEqualTo(OrderStateBoEnum.PARTIALLY_EXECUTED);
    }

    @Test
    public void test_convert_when_closed_then_stateFullyExecuted() {
        // When
        final OrderStateBoEnum actual = converter.convert(Pair.of("closed", 1));

        // Then
        then(actual).isEqualTo(OrderStateBoEnum.FULLY_EXECUTED);
    }

    @Test
    public void test_convert_when_canceled_and_noTrades_then_stateFullyCanceled() {
        // When
        final OrderStateBoEnum actual = converter.convert(Pair.of("canceled", 0));

        // Then
        then(actual).isEqualTo(OrderStateBoEnum.FULLY_CANCELED);
    }

    @Test
    public void test_convert_when_canceled_and_tradesExist_then_statePartiallyExecutedThenCanceled() {
        // When
        final OrderStateBoEnum actual = converter.convert(Pair.of("canceled", 1));

        // Then
        then(actual).isEqualTo(OrderStateBoEnum.PARTIALLY_EXECUTED_THEN_CANCELED);
    }

    @Test
    public void test_convert_when_unknownKrakenStatus_then_exception() {
        // When
        final Throwable caughtThrowable = catchThrowable(() -> converter.convert(Pair.of("partially_executed", 2)));

        // Then
        then(caughtThrowable) //
                .isInstanceOf(IllegalArgumentException.class) //
                .hasMessage("Unsupported Kraken order status [partially_executed]");
    }
}
