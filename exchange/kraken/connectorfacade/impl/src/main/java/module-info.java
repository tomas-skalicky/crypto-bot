module com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl {
    requires com.google.common;
    requires com.skalicky.cryptobot.exchange.kraken.connector.api;
    requires com.skalicky.cryptobot.exchange.kraken.connectorfacade.api;
    requires com.skalicky.cryptobot.exchange.shared.connectorfacade.api;
    requires com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api;
    requires org.apache.commons.collections4;
    requires org.apache.commons.lang3;
    requires org.jetbrains.annotations;

    exports com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.converter;
    exports com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl.logic;
}
