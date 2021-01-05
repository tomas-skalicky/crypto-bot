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

package com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.converter;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.BDDAssertions.then;

public class LocalDateTimeToEpochSecondLongConverterUTest {

    @Test
    public void test_convert_when_timeZoneIsNonZero_then_timeZoneIsConsidered() {
        // Given
        final LocalDateTime dateTime = LocalDateTime.of(
                2020, 3, 8, 10, 30, 1);

        // When
        final Long epochSeconds = new LocalDateTimeToEpochSecondLongConverter().convert(dateTime);

        // Then
        then(epochSeconds).isEqualTo(1583659801);
    }

    @Test
    public void test_convert_when_nanoSecondsAreNonZero_then_nanoSecondsIsIgnored() {
        // Given
        final LocalDateTime dateTime = LocalDateTime.of(
                2020, 3, 8, 10, 30, 1, 999_999_999);

        // When
        final Long epochSeconds = new LocalDateTimeToEpochSecondLongConverter().convert(dateTime);

        // Then
        then(epochSeconds).isEqualTo(1583659801);
    }
}
