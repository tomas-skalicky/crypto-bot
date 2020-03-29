module com.skalicky.cryptobot.businesslogic.impl {
    requires com.google.common;
    requires com.skalicky.cryptobot.businesslogic.api;
    requires com.skalicky.cryptobot.exchange.slack.connectorfacade.api;
    requires com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports com.skalicky.cryptobot.businesslogic.impl;
    exports com.skalicky.cryptobot.businesslogic.impl.datetime;
}
