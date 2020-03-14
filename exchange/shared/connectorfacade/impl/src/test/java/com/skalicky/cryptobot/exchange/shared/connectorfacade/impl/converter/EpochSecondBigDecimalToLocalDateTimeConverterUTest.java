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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EpochSecondBigDecimalToLocalDateTimeConverterUTest {

    @Test
    public void test_serialize_when_germanTimeZone_then_resultLocalDateTimeWithGermanTimeZone() {
        final BigDecimal epochSecondsWithoutNanos = BigDecimal.valueOf(1583659801);

        final LocalDateTime actualDateTime =
                new EpochSecondBigDecimalToLocalDateTimeConverter().convert(epochSecondsWithoutNanos);

        final LocalDateTime expectedDateTime = LocalDateTime.of(
                2020, 3, 8, 10, 30, 1);
        assertThat(actualDateTime).isEqualTo(expectedDateTime);
    }

    @Test
    public void test_serialize_when_nanoSecondsInEpochAreNonZero_then_nanoSecondsInResultAsWell() {
        final BigDecimal epochSecondsWithNanos = new BigDecimal("1583659801.999999999");

        final LocalDateTime actualDateTime =
                new EpochSecondBigDecimalToLocalDateTimeConverter().convert(epochSecondsWithNanos);

        final LocalDateTime expectedDateTime = LocalDateTime.of(
                2020, 3, 8, 10, 30, 1, 999_999_999);
        assertThat(actualDateTime).isEqualTo(expectedDateTime);
    }
}
