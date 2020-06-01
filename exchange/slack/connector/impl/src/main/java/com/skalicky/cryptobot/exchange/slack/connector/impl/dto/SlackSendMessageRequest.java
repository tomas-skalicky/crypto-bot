package com.skalicky.cryptobot.exchange.slack.connector.impl.dto;

import org.jetbrains.annotations.NotNull;

public class SlackSendMessageRequest {
    @NotNull
    private final String text;

    public SlackSendMessageRequest(@NotNull final String text) {
        this.text = text;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    @Override
    public String toString() {
        return "SlackSendMessageRequest{" +
                "text='" + text + '\'' +
                '}';
    }
}
