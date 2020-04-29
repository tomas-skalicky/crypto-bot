/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2020 Tomas Skalicky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.skalicky.cryptobot.exchange.poloniex.connector.impl.restapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.dto.PoloniexReturnTickerDto;
import com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.dto.PoloniexReturnTickerDtoBuilder;
import com.skalicky.cryptobot.exchange.poloniex.connector.impl.restapi.logic.PoloniexPublicApiConnectorImpl;
import com.skalicky.cryptobot.exchange.shared.connector.impl.logic.RestConnectorSupport;
import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PoloniexPublicApiConnectorImplUTest {
    @Nonnull
    public static final String PUBLIC_API_URI_STRING = "http://localhost/";

    @Nonnull
    private final RestConnectorSupport restConnectorSupport = Mockito.mock(RestConnectorSupport.class);
    @Nonnull
    private final PoloniexPublicApiConnectorImpl poloniexPublicApiConnectorImpl = new PoloniexPublicApiConnectorImpl(
            new URI(PUBLIC_API_URI_STRING), restConnectorSupport);

    public PoloniexPublicApiConnectorImplUTest() throws URISyntaxException {
    }

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(restConnectorSupport);
        Mockito.reset(restConnectorSupport);
    }

    @Test
    public void test_returnTicker_when_uriIsValid_then_tickersAreReturned() throws URISyntaxException {

        final PoloniexReturnTickerDto btcXrpTicker = PoloniexReturnTickerDtoBuilder.aPoloniexReturnTickerDto()
                .withId(117)
                .withLast(new BigDecimal("0.00002693"))
                .withLowestAsk(new BigDecimal("0.00002692"))
                .withHighestBid(new BigDecimal("0.00002691"))
                .withPercentChange(new BigDecimal("0.01891789"))
                .withBaseVolume(new BigDecimal("490.97365508"))
                .withQuoteVolume(new BigDecimal("18028762.12187298"))
                .withFrozen(false)
                .withHigh24hr(new BigDecimal("0.00002807"))
                .withLow24hr(new BigDecimal("0.00002641"))
                .build();
        final PoloniexReturnTickerDto usdtBtcTicker = PoloniexReturnTickerDtoBuilder.aPoloniexReturnTickerDto()
                .withId(121)
                .withLast(new BigDecimal("7988.65608131"))
                .withLowestAsk(new BigDecimal("7989.32961488"))
                .withHighestBid(new BigDecimal("7988.66780100"))
                .withPercentChange(new BigDecimal("0.03206780"))
                .withBaseVolume(new BigDecimal("21888460.33185739"))
                .withQuoteVolume(new BigDecimal("2807.18940096"))
                .withFrozen(false)
                .withHigh24hr(new BigDecimal("7989.32961488"))
                .withLow24hr(new BigDecimal("7666.48331669"))
                .build();
        final String btcXrpTickerName = "BTC_XRP";
        final String usdtBtcTickerName = "USDT_BTC";
        final ImmutableMap<String, PoloniexReturnTickerDto> expectedResponsePayload =
                ImmutableMap.of(btcXrpTickerName, btcXrpTicker, usdtBtcTickerName, usdtBtcTicker);
        final URI uri = new URI(PUBLIC_API_URI_STRING + "?command=returnTicker");
        final ImmutableMultivaluedMap<String, Object> requestHeaders = ImmutableMultivaluedMap.empty();
        when(restConnectorSupport.getAcceptingJson(eq(uri), eq(requestHeaders), any(TypeReference.class)))
                .thenReturn(expectedResponsePayload);

        final ImmutableMap<String, PoloniexReturnTickerDto> actualResponsePayload =
                poloniexPublicApiConnectorImpl.returnTicker();

        verify(restConnectorSupport).getAcceptingJson(eq(uri), eq(requestHeaders), any(TypeReference.class));

        assertThat(actualResponsePayload).containsOnlyKeys(btcXrpTickerName, usdtBtcTickerName);
        assertThat(actualResponsePayload.get(btcXrpTickerName).getId()).isEqualTo(117);
        assertThat(actualResponsePayload.get(btcXrpTickerName).getLast()).isEqualTo(new BigDecimal("0.00002693"));
        assertThat(actualResponsePayload.get(btcXrpTickerName).getFrozen()).isFalse();
        assertThat(actualResponsePayload.get(usdtBtcTickerName).getId()).isEqualTo(121);
        assertThat(actualResponsePayload.get(usdtBtcTickerName).getLast()).isEqualTo(new BigDecimal("7988.65608131"));
        assertThat(actualResponsePayload.get(usdtBtcTickerName).getFrozen()).isFalse();
    }
}
