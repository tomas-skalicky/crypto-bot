module com.skalicky.cryptobot.exchange.slack.connector.impl {
    requires com.fasterxml.jackson.core;
    requires com.skalicky.cryptobot.exchange.shared.connector.impl;
    requires com.skalicky.cryptobot.exchange.slack.connector.api;
    requires jersey.common;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.slf4j;

    exports com.skalicky.cryptobot.exchange.slack.connector.impl.logic;

    // Necessary, otherwise:
    // Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Invalid type definition for type
    // `com.skalicky.cryptobot.exchange.slack.connector.impl.dto.SlackSendMessageRequest`: Failed to construct
    // BeanSerializer for [simple type, class
    // com.skalicky.cryptobot.exchange.slack.connector.impl.dto.SlackSendMessageRequest]:
    // (java.lang.reflect.InaccessibleObjectException) Unable to make public
    // java.lang.String com.skalicky.cryptobot.exchange.slack.connector.impl.dto.SlackSendMessageRequest.getText()
    // accessible: module com.skalicky.cryptobot.exchange.slack.connector.impl does not
    // "exports com.skalicky.cryptobot.exchange.slack.connector.impl.dto" to module com.fasterxml.jackson.databind
    opens com.skalicky.cryptobot.exchange.slack.connector.impl.dto;
}
