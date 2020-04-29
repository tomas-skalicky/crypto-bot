package com.skalicky.cryptobot.exchange.poloniex.connector.impl.restapi.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.dto.PoloniexReturnTickerDto;
import com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.logic.PoloniexPublicApiConnector;
import com.skalicky.cryptobot.exchange.shared.connector.impl.logic.RestConnectorSupport;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;

public class PoloniexPublicApiConnectorImpl implements PoloniexPublicApiConnector {
    @Nonnull
    private static final String COMMAND_QUERY_PARAMETER_NAME = "command";
    @Nonnull
    private static final TypeReference<ImmutableMap<String, PoloniexReturnTickerDto>> returnTickerResponseTypeReference =
            new TypeReference<>() {
            };

    @Nonnull
    private final URI publicApiUri;
    @Nonnull
    private final RestConnectorSupport restConnectorSupport;

    public PoloniexPublicApiConnectorImpl(@Nonnull final URI publicApiUri,
                                          @Nonnull final RestConnectorSupport restConnectorSupport) {
        this.publicApiUri = publicApiUri;
        this.restConnectorSupport = restConnectorSupport;
    }

    // FIXME Tomas 2 Poloniex: cover with tests
    @Nonnull
    @Override
    public ImmutableMap<String, PoloniexReturnTickerDto> returnTicker() {
        try {
            final URI uri = new URIBuilder(publicApiUri)
                    .addParameter(COMMAND_QUERY_PARAMETER_NAME, "returnTicker")
                    .build();
            return restConnectorSupport.getAcceptingJson(uri, ImmutableMultivaluedMap.empty(),
                    returnTickerResponseTypeReference);
        } catch (@Nonnull final URISyntaxException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
