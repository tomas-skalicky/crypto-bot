module com.skalicky.cryptobot.application {
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires com.skalicky.cryptobot.businesslogic.api;
    requires com.skalicky.cryptobot.businesslogic.impl;
    requires com.skalicky.cryptobot.exchange.kraken.connector.impl;
    requires com.skalicky.cryptobot.exchange.kraken.connectorfacade.api;
    requires com.skalicky.cryptobot.exchange.kraken.connectorfacade.impl;
    requires com.skalicky.cryptobot.exchange.shared.connector.impl;
    requires com.skalicky.cryptobot.exchange.shared.connectorfacade.api;
    requires com.skalicky.cryptobot.exchange.shared.connectorfacade.impl;
    requires com.skalicky.cryptobot.exchange.slack.connector.impl;
    requires com.skalicky.cryptobot.exchange.slack.connectorfacade.api;
    requires com.skalicky.cryptobot.exchange.slack.connectorfacade.impl;
    requires com.skalicky.cryptobot.exchange.tradingplatform.connectorfacade.api;
    requires jcommander;
    requires org.apache.commons.lang3;
    requires org.jetbrains.annotations;

    opens com.skalicky.cryptobot.application.input to jcommander;
}
