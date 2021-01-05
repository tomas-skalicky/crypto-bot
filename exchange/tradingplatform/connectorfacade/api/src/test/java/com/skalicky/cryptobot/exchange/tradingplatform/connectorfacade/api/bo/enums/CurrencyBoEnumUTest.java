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

package com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api.bo.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.then;

public class CurrencyBoEnumUTest {

    @Test
    public void test_getByLabel_when_existingNonNullCurrencyLabel_then_enumValue() {
        // When
        final CurrencyBoEnum actual = CurrencyBoEnum.getByLabel("EUR");

        // Then
        then(actual).isEqualTo(CurrencyBoEnum.EUR);
    }

    @Test
    public void test_getByLabel_when_null_then_others() {
        // When
        final CurrencyBoEnum actual = CurrencyBoEnum.getByLabel(null);

        // Then
        then(actual).isEqualTo(CurrencyBoEnum.OTHERS);
    }

    @Test
    public void test_getByLabel_when_nonExistingCurrencyLabel_then_exception() {
        // When
        final Throwable caughtThrowable = catchThrowable(() -> CurrencyBoEnum.getByLabel("LTC"));

        // Then
        then(caughtThrowable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported label [LTC]");
    }
}
