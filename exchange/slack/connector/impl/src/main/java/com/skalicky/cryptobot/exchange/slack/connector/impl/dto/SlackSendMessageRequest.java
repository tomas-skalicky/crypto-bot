package com.skalicky.cryptobot.exchange.slack.connector.impl.dto;

import javax.annotation.Nonnull;

public class SlackSendMessageRequest {
    @Nonnull
    private final String text;

    public SlackSendMessageRequest(@Nonnull final String text) {
        this.text = text;
    }

    @Nonnull
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "SlackSendMessageRequest{" +
                "text='" + text + '\'' +
                '}';
    }
}
