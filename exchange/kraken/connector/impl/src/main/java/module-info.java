module com.skalicky.cryptobot.exchange.kraken.connector.impl {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires com.skalicky.cryptobot.exchange.kraken.connector.api;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports com.skalicky.cryptobot.exchange.kraken.connector.impl.logic;
    exports edu.self.kraken.api;
}
