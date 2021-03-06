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

package com.skalicky.cryptobot.exchange.slack.connectorfacade.impl.logic;

import com.skalicky.cryptobot.exchange.slack.connector.api.logic.SlackConnector;
import com.skalicky.cryptobot.exchange.slack.connectorfacade.api.logic.SlackFacade;
import org.jetbrains.annotations.NotNull;

public class SlackFacadeImpl implements SlackFacade {

    @NotNull
    private final SlackConnector slackConnector;

    public SlackFacadeImpl(@NotNull final SlackConnector slackConnector) {
        this.slackConnector = slackConnector;
    }

    @Override
    public void sendMessage(@NotNull final String text) {
        this.slackConnector.sendMessage(text);
    }
}
