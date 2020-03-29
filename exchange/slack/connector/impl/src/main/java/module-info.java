module com.skalicky.cryptobot.exchange.slack.connector.impl {
    requires com.skalicky.cryptobot.exchange.shared.connector.impl;
    requires com.skalicky.cryptobot.exchange.slack.connector.api;
    requires org.jetbrains.annotations;


    exports com.skalicky.cryptobot.exchange.slack.connector.impl.logic;
}
