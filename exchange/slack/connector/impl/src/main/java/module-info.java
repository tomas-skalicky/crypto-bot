module com.skalicky.cryptobot.exchange.slack.connector.impl {
    requires com.fasterxml.jackson.core;
    requires com.skalicky.cryptobot.exchange.shared.connector.impl;
    requires com.skalicky.cryptobot.exchange.slack.connector.api;
    requires jersey.common;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports com.skalicky.cryptobot.exchange.slack.connector.impl.logic;
}
