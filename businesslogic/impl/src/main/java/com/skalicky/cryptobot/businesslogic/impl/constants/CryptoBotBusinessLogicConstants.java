/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2021 Tomas Skalicky
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

package com.skalicky.cryptobot.businesslogic.impl.constants;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;

public final class CryptoBotBusinessLogicConstants {

    @NotNull
    public static final DateTimeFormatter NOTIFICATION_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM. HH:mm");

    private CryptoBotBusinessLogicConstants() {
    }
}
