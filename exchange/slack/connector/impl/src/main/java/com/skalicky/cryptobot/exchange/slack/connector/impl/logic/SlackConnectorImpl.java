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

package com.skalicky.cryptobot.exchange.slack.connector.impl.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.skalicky.cryptobot.exchange.shared.connector.impl.logic.RestConnectorSupport;
import com.skalicky.cryptobot.exchange.slack.connector.api.logic.SlackConnector;
import com.skalicky.cryptobot.exchange.slack.connector.impl.dto.SlackSendMessageRequest;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

public class SlackConnectorImpl implements SlackConnector {
    @Nonnull
    private static final Logger logger = LoggerFactory.getLogger(SlackConnectorImpl.class);
    @Nonnull
    private static final TypeReference<String> stringTypeReference = new TypeReference<>() {
    };

    @Nonnull
    private final RestConnectorSupport restConnectorSupport;
    @Nullable
    private final String webhookUriString;
    @Nullable
    private URI webhookUri;

    public SlackConnectorImpl(@Nonnull final RestConnectorSupport restConnectorSupport,
                              @Nullable final String webhookUriString) {
        this.restConnectorSupport = restConnectorSupport;
        this.webhookUriString = webhookUriString;
    }

    @Nullable
    private URI determineWebhookUri() {
        if (this.webhookUriString == null) {
            logger.info("No Slack Webhook URL provided");
            return null;
        } else {
            if (this.webhookUri == null) {
                try {
                    this.webhookUri = new URI(webhookUriString);
                } catch (URISyntaxException ex) {
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
            }
            return this.webhookUri;
        }
    }

    @Override
    public void sendMessage(@Nonnull final String text) {
        final URI uri = determineWebhookUri();
        if (uri != null) {
            restConnectorSupport.postJsonAcceptingJson(uri, ImmutableMultivaluedMap.empty(),
                    new SlackSendMessageRequest(text), stringTypeReference);
        }
    }
}
