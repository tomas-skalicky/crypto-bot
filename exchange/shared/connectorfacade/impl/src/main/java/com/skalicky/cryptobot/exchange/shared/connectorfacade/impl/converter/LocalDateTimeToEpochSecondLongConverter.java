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

import com.skalicky.cryptobot.exchange.shared.connectorfacade.api.converter.NonnullConverter;
import com.skalicky.cryptobot.exchange.shared.connectorfacade.impl.util.DateTimeUtil;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public final class LocalDateTimeToEpochSecondLongConverter implements NonnullConverter<LocalDateTime, Long> {

    @Override
    @NotNull
    public Long convert(@NotNull final LocalDateTime localDateTime) {
        return ZonedDateTime.of(localDateTime, DateTimeUtil.BERLIN_ZONE_ID).toEpochSecond();
    }
}
