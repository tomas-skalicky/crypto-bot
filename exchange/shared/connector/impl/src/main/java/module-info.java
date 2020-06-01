module com.skalicky.cryptobot.exchange.shared.connector.impl {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.ws.rs;
    requires jersey.client;
    requires jersey.common;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports com.skalicky.cryptobot.exchange.shared.connector.impl.logic;
}
