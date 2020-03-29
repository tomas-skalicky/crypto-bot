module com.skalicky.cryptobot.exchange.slack.connectorfacade.impl {
    requires com.skalicky.cryptobot.exchange.slack.connector.api;
    requires com.skalicky.cryptobot.exchange.slack.connectorfacade.api;
    requires org.jetbrains.annotations;

    exports com.skalicky.cryptobot.exchange.slack.connectorfacade.impl.logic;
}
