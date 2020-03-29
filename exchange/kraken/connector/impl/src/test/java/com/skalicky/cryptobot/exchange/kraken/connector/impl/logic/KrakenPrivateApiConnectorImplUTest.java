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

package com.skalicky.cryptobot.exchange.kraken.connector.impl.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenAddOrderResultDescriptionDto;
import edu.self.kraken.api.KrakenApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPrivateApiConnectorImplUTest {
    @NotNull
    private final KrakenApi krakenApi = Mockito.mock(KrakenApi.class);
    @NotNull
    private final KrakenPrivateApiConnectorImpl krakenPrivateApiConnectorImpl = new KrakenPrivateApiConnectorImpl(
            krakenApi, new ObjectMapper());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenApi);
        Mockito.reset(krakenApi);
    }

    @Test
    public void test_balance_when_dataForPairInResponseFile_then_askPriceReturned_and_bidPriceReturned() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"ZEUR\": \"100.7896\"," +
                "        \"XXBT\": \"0.0000000030\"," +
                "        \"BCH\":  \"0.0000000000\"" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(KrakenApi.Method.BALANCE)).thenReturn(krakenApiResponse);

        final var connectorResponse = krakenPrivateApiConnectorImpl.balance();

        verify(krakenApi).queryPrivate(KrakenApi.Method.BALANCE);

        assertThat(connectorResponse.getError()).isEmpty();
        assertThat(connectorResponse.getResult()).hasSize(3);
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        assertThat(connectorResponse.getResult().get("ZEUR")).isEqualTo(new BigDecimal("100.7896"));
        assertThat(connectorResponse.getResult().get("XXBT")).isEqualTo(new BigDecimal("0.0000000030"));
        assertThat(connectorResponse.getResult().get("BCH")).isEqualTo(new BigDecimal("0.0000000000"));
    }

    @Test
    public void test_addOrder_when_everythingOk_then_noError() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"descr\": {" +
                "            \"order\": \"buy 0.00200000 XBTEUR @ limit 6000.9\"" +
                "        }," +
                "        \"txid\": [" +
                "            \"OXXXXO-YYYYY-33244K\"" +
                "        ]" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse = krakenPrivateApiConnectorImpl.addOrder(
                "XBTEUR", "buy", "limit", new BigDecimal("6000.9"),
                new BigDecimal("0.002"), ImmutableList.of("fciq"), 1);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap());

        assertThat(connectorResponse.getError()).isEmpty();
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        // isNotNull() to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult().getDescr())
                .isNotNull()
                .extracting(KrakenAddOrderResultDescriptionDto::getOrder)
                .isEqualTo("buy 0.00200000 XBTEUR @ limit 6000.9");
        // isNotNull() to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult().getTxid()) //
                .isNotNull() //
                .containsExactly("OXXXXO-YYYYY-33244K");
    }

    @Test
    public void test_addOrder_when_insufficientPermissions_then_correspondingError() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": [\"EGeneral:Permission denied\"]" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse = krakenPrivateApiConnectorImpl.addOrder(
                "LTCEUR", "buy", "market", new BigDecimal(40),
                BigDecimal.ONE, ImmutableList.<String>builder().build(), 1);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap());

        // isNotNull() to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getError()) //
                .isNotNull() //
                .containsExactly("EGeneral:Permission denied");
        assertThat(connectorResponse.getResult()).isNull();
    }

    @Test
    public void test_openOrders_when_includeTradeIsFalse_then_noTradesRetrieved() throws Exception {

        final var orderId = "XXXXXX-YYYY5-FFFFFF";
        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"open\": {" +
                "            \"XXXXXX-YYYY5-FFFFFF\": {" +
                "                \"refid\": null," +
                "                \"userref\": 0," +
                "                \"status\": \"open\"," +
                "                \"opentm\": 1583703494.5067," +
                "                \"starttm\": 0," +
                "                \"expiretm\": 1583833094," +
                "                \"descr\": {" +
                "                    \"pair\": \"XBTEUR\"," +
                "                    \"type\": \"buy\"," +
                "                    \"ordertype\": \"limit\"," +
                "                    \"price\": \"7230.4\"," +
                "                    \"price2\": \"0\"," +
                "                    \"leverage\": \"none\"," +
                "                    \"order\": \"buy 0.012345 XBTEUR @ limit 7230.4\"," +
                "                    \"close\": \"\"" +
                "                }," +
                "                \"vol\": \"0.012345\"," +
                "                \"vol_exec\": \"0.00000000\",\n" +
                "                \"cost\": \"0.00000\",\n" +
                "                \"fee\": \"0.00000\",\n" +
                "                \"price\": \"0.00000\",\n" +
                "                \"stopprice\": \"0.00000\",\n" +
                "                \"limitprice\": \"0.00000\",\n" +
                "                \"misc\": \"\",\n" +
                "                \"oflags\": \"fciq\"" +
                "            }" +
                "        }" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse =
                krakenPrivateApiConnectorImpl.openOrders(false);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap());

        assertThat(connectorResponse.getError()).isEmpty();
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        assertThat(connectorResponse.getResult().getOpen()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull()
                .containsOnlyKeys(orderId);
        final var openOrder = connectorResponse.getResult().getOpen().get(orderId);
        assertThat(openOrder.getStatus()).isEqualTo("open");
        assertThat(openOrder.getOpentm()).isEqualTo(new BigDecimal("1583703494.5067"));
        assertThat(openOrder.getExpiretm()).isEqualTo(new BigDecimal("1583833094"));
        assertThat(openOrder.getVol()).isEqualTo(new BigDecimal("0.012345"));
        assertThat(openOrder.getVol_exec()).isEqualTo(new BigDecimal("0.00000000"));
        assertThat(openOrder.getPrice()).isEqualTo(new BigDecimal("0.00000"));
        assertThat(openOrder.getOflags()).isEqualTo("fciq");
        assertThat(openOrder.getTrades()).isNull();
        final var orderDescription = openOrder.getDescr();
        assertThat(orderDescription).isNotNull();
        assertThat(orderDescription.getPair()).isEqualTo("XBTEUR");
        assertThat(orderDescription.getType()).isEqualTo("buy");
        assertThat(orderDescription.getOrdertype()).isEqualTo("limit");
        assertThat(orderDescription.getPrice()).isEqualTo(new BigDecimal("7230.4"));
    }

    @Test
    public void test_openOrders_when_includeTradeIsTrue_then_tradesRetrieved() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"open\": {" +
                "            \"XXXXXX-YYYY5-1234ZZ\": {" +
                "                \"refid\": null," +
                "                \"userref\": 0," +
                "                \"status\": \"open\"," +
                "                \"opentm\": 1583703494.1067," +
                "                \"starttm\": 0," +
                "                \"expiretm\": 1583833094," +
                "                \"descr\": {" +
                "                    \"pair\": \"XBTEUR\"," +
                "                    \"type\": \"buy\"," +
                "                    \"ordertype\": \"limit\"," +
                "                    \"price\": \"7230.4\"," +
                "                    \"price2\": \"0\"," +
                "                    \"leverage\": \"none\"," +
                "                    \"order\": \"buy 0.012345 XBTEUR @ limit 7230.4\"," +
                "                    \"close\": \"\"" +
                "                }," +
                "                \"vol\": \"0.12345\"," +
                "                \"vol_exec\": \"0.065\"," +
                "                \"cost\": \"49.9\"," +
                "                \"fee\": \"0\"," +
                "                \"price\": \"7230.3\"," +
                "                \"stopprice\": \"0.00000\"," +
                "                \"limitprice\": \"0.00000\"," +
                "                \"misc\": \"\"," +
                "                \"oflags\": \"fciq\"," +
                "                \"trades\": [" +
                "                    \"AAAAAA-YY7ZZ-ABCD44\"" +
                "                ]" +
                "            }" +
                "        }" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse =
                krakenPrivateApiConnectorImpl.openOrders(false);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult().getOpen()).isNotNull();
        final var openOrder = connectorResponse.getResult().getOpen().get("XXXXXX-YYYY5-1234ZZ");
        assertThat(openOrder.getTrades()).containsExactly("AAAAAA-YY7ZZ-ABCD44");
    }

    @Test
    public void test_openOrders_when_twoRequests_then_bothRequestsDeserialized() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"open\": {" +
                "            \"AAAABB-YY7YY-4ZZZZZ\": {" +
                "                \"status\": \"open\"" +
                "            }," +
                "            \"BBBBCC-YY7YY-4ZZZZZ\": {" +
                "                \"status\": \"open\"" +
                "            }" +
                "        }" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse = krakenPrivateApiConnectorImpl.openOrders(false);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        assertThat(connectorResponse.getResult().getOpen()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull() //
                .containsOnlyKeys("AAAABB-YY7YY-4ZZZZZ", "BBBBCC-YY7YY-4ZZZZZ");
    }

    @Test
    public void test_closedOrders_when_includeTradeIsFalse_then_noTradesRetrieved() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"closed\": {" +
                "            \"XXXXXX-YYYY5-ZZZZZZ\": {" +
                "                \"refid\": null," +
                "                \"userref\": 0," +
                "                \"status\": \"closed\"," +
                "                \"reason\": null," +
                "                \"opentm\": 1583703494.1067," +
                "                \"closetm\": 1583704373.4438," +
                "                \"starttm\": 0," +
                "                \"expiretm\": 1583833094," +
                "                \"descr\": {" +
                "                    \"pair\": \"XBTEUR\"," +
                "                    \"type\": \"buy\"," +
                "                    \"ordertype\": \"limit\"," +
                "                    \"price\": \"7230.4\"," +
                "                    \"price2\": \"0\"," +
                "                    \"leverage\": \"none\"," +
                "                    \"order\": \"buy 0.012345 XBTEUR @ limit 7230.4\"," +
                "                    \"close\": \"\"" +
                "                }," +
                "                \"vol\": \"0.012345\"," +
                "                \"vol_exec\": \"0.012345\"," +
                "                \"cost\": \"49.9\"," +
                "                \"fee\": \"0\"," +
                "                \"price\": \"7230.3\"," +
                "                \"stopprice\": \"0.00000\"," +
                "                \"limitprice\": \"0.00000\"," +
                "                \"misc\": \"\"," +
                "                \"oflags\": \"fciq\"" +
                "            }" +
                "        }," +
                "        \"count\": 1" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse =
                krakenPrivateApiConnectorImpl.closedOrders(false, 1583703494L);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap());

        assertThat(connectorResponse.getError()).isEmpty();
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        assertThat(connectorResponse.getResult().getCount()).isEqualTo(1);
        assertThat(connectorResponse.getResult().getClosed()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull()
                .containsOnlyKeys("XXXXXX-YYYY5-ZZZZZZ");
        final var closedOrder = connectorResponse.getResult().getClosed().get("XXXXXX-YYYY5-ZZZZZZ");
        assertThat(closedOrder.getStatus()).isEqualTo("closed");
        assertThat(closedOrder.getOpentm()).isEqualTo(new BigDecimal("1583703494.1067"));
        assertThat(closedOrder.getClosetm()).isEqualTo(new BigDecimal("1583704373.4438"));
        assertThat(closedOrder.getExpiretm()).isEqualTo(new BigDecimal("1583833094"));
        assertThat(closedOrder.getVol()).isEqualTo(new BigDecimal("0.012345"));
        assertThat(closedOrder.getVol_exec()).isEqualTo(new BigDecimal("0.012345"));
        assertThat(closedOrder.getPrice()).isEqualTo(new BigDecimal("7230.3"));
        assertThat(closedOrder.getOflags()).isEqualTo("fciq");
        assertThat(closedOrder.getTrades()).isNull();
        final var orderDescription = closedOrder.getDescr();
        assertThat(orderDescription).isNotNull();
        assertThat(orderDescription.getPair()).isEqualTo("XBTEUR");
        assertThat(orderDescription.getType()).isEqualTo("buy");
        assertThat(orderDescription.getOrdertype()).isEqualTo("limit");
        assertThat(orderDescription.getPrice()).isEqualTo(new BigDecimal("7230.4"));
    }

    @Test
    public void test_closedOrders_when_includeTradeIsTrue_then_tradesRetrieved() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"closed\": {" +
                "            \"XXXXXX-YYYY5-121Z44\": {" +
                "                \"refid\": null," +
                "                \"userref\": 0," +
                "                \"status\": \"closed\"," +
                "                \"reason\": null," +
                "                \"opentm\": 1583703494.1067," +
                "                \"closetm\": 1583704373.4438," +
                "                \"starttm\": 0," +
                "                \"expiretm\": 1583833094," +
                "                \"descr\": {" +
                "                    \"pair\": \"XBTEUR\"," +
                "                    \"type\": \"buy\"," +
                "                    \"ordertype\": \"limit\"," +
                "                    \"price\": \"7230.4\"," +
                "                    \"price2\": \"0\"," +
                "                    \"leverage\": \"none\"," +
                "                    \"order\": \"buy 0.012345 XBTEUR @ limit 7230.4\"," +
                "                    \"close\": \"\"" +
                "                }," +
                "                \"vol\": \"0.012345\"," +
                "                \"vol_exec\": \"0.012345\"," +
                "                \"cost\": \"49.9\"," +
                "                \"fee\": \"0\"," +
                "                \"price\": \"7230.3\"," +
                "                \"stopprice\": \"0.00000\"," +
                "                \"limitprice\": \"0.00000\"," +
                "                \"misc\": \"\"," +
                "                \"oflags\": \"fciq\"," +
                "                \"trades\": [" +
                "                    \"AAAAAA-YY7YY-4ZZZZZ\"" +
                "                ]" +
                "            }" +
                "        }," +
                "        \"count\": 1" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse =
                krakenPrivateApiConnectorImpl.closedOrders(false, 1583703494L);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult().getClosed()).isNotNull();
        final var closedOrder = connectorResponse.getResult().getClosed().get("XXXXXX-YYYY5-121Z44");
        assertThat(closedOrder.getTrades()).containsExactly("AAAAAA-YY7YY-4ZZZZZ");
    }

    @Test
    public void test_closedOrders_when_twoRequests_then_bothRequestsDeserialized() throws Exception {

        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": []," +
                "    \"result\": {" +
                "        \"closed\": {" +
                "            \"AAAAAA-YY7YY-4ZZZZZ\": {" +
                "                \"status\": \"closed\"" +
                "            }," +
                "            \"BBBBBB-YY7YY-4ZZZZZ\": {" +
                "                \"status\": \"canceled\"" +
                "            }" +
                "        }," +
                "        \"count\": 2" +
                "    }" +
                "}";
        // @formatter:on
        when(krakenApi.queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap())).thenReturn(krakenApiResponse);

        final var connectorResponse =
                krakenPrivateApiConnectorImpl.closedOrders(false, 1583703494L);

        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        assertThat(connectorResponse.getResult()).isNotNull();
        assertThat(connectorResponse.getResult().getClosed()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull() //
                .containsOnlyKeys("AAAAAA-YY7YY-4ZZZZZ", "BBBBBB-YY7YY-4ZZZZZ");
        assertThat(connectorResponse.getResult().getCount()).isEqualTo(2);
    }
}
