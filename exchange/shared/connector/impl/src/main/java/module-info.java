module com.skalicky.cryptobot.exchange.shared.connector.impl {
    requires java.ws.rs;
    requires jersey.client;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports com.skalicky.cryptobot.exchange.shared.connector.impl.logic;
}
