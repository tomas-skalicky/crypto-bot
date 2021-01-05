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
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenAddOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenClosedOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOpenOrderResultDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenOrderDescriptionDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import edu.self.kraken.api.KrakenApi;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

public class KrakenPrivateApiConnectorImplUTest {
    @NotNull
    private final KrakenApi krakenApi = mock(KrakenApi.class);
    @NotNull
    private final KrakenPrivateApiConnectorImpl krakenPrivateApiConnectorImpl = new KrakenPrivateApiConnectorImpl(
            krakenApi, new ObjectMapper());

    @AfterEach
    public void assertAndCleanMocks() {
        verifyNoMoreInteractions(krakenApi);
        reset(krakenApi);
    }

    @Test
    public void test_balance_when_responsePayloadIsValid_then_askPriceReturned_and_bidPriceReturned() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(KrakenApi.Method.BALANCE)).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<Map<String, BigDecimal>> connectorResponse = krakenPrivateApiConnectorImpl.balance();

        // Then
        verify(krakenApi).queryPrivate(KrakenApi.Method.BALANCE);

        then(connectorResponse.getError()).isEmpty();
        then(connectorResponse.getResult()).hasSize(3);
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        then(connectorResponse.getResult().get("ZEUR")).isEqualTo(new BigDecimal("100.7896"));
        then(connectorResponse.getResult().get("XXBT")).isEqualTo(new BigDecimal("0.0000000030"));
        then(connectorResponse.getResult().get("BCH")).isEqualTo(new BigDecimal("0.0000000000"));
    }

    @Test
    public void test_addOrder_when_everythingOk_then_noError() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenAddOrderResultDto> connectorResponse = krakenPrivateApiConnectorImpl.addOrder(
                "XBTEUR", "buy", "limit", new BigDecimal("6000.9"),
                new BigDecimal("0.002"), ImmutableList.of("fciq"), 1);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap());

        then(connectorResponse.getError()).isEmpty();
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        // isNotNull() to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult().getDescr())
                .isNotNull()
                .extracting(KrakenAddOrderResultDescriptionDto::getOrder)
                .isEqualTo("buy 0.00200000 XBTEUR @ limit 6000.9");
        // isNotNull() to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult().getTxid()) //
                .isNotNull() //
                .containsExactly("OXXXXO-YYYYY-33244K");
    }

    @Test
    public void test_addOrder_when_insufficientPermissions_then_correspondingError() throws Exception {

        // Given
        // @formatter:off
        final var krakenApiResponse = "{" +
                "    \"error\": [\"EGeneral:Permission denied\"]" +
                "}";
        // @formatter:on
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenAddOrderResultDto> connectorResponse = krakenPrivateApiConnectorImpl.addOrder(
                "LTCEUR", "buy", "market", new BigDecimal(40),
                BigDecimal.ONE, ImmutableList.of(), 1);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.ADD_ORDER), anyMap());

        // isNotNull() to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getError()) //
                .isNotNull() //
                .containsExactly("EGeneral:Permission denied");
        then(connectorResponse.getResult()).isNull();
    }

    @Test
    public void test_openOrders_when_includeTradeIsFalse_then_noTradesRetrieved() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenOpenOrderResultDto> connectorResponse =
                krakenPrivateApiConnectorImpl.openOrders(false);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap());

        then(connectorResponse.getError()).isEmpty();
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        then(connectorResponse.getResult().getOpen()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull()
                .containsOnlyKeys(orderId);
        final KrakenOpenOrderDto openOrder = connectorResponse.getResult().getOpen().get(orderId);
        then(openOrder.getStatus()).isEqualTo("open");
        then(openOrder.getOpentm()).isEqualTo(new BigDecimal("1583703494.5067"));
        then(openOrder.getExpiretm()).isEqualTo(new BigDecimal("1583833094"));
        then(openOrder.getVol()).isEqualTo(new BigDecimal("0.012345"));
        then(openOrder.getVol_exec()).isEqualTo(new BigDecimal("0.00000000"));
        then(openOrder.getPrice()).isEqualTo(new BigDecimal("0.00000"));
        then(openOrder.getOflags()).isEqualTo("fciq");
        then(openOrder.getTrades()).isNull();
        final KrakenOrderDescriptionDto orderDescription = openOrder.getDescr();
        then(orderDescription).isNotNull();
        then(orderDescription.getPair()).isEqualTo("XBTEUR");
        then(orderDescription.getType()).isEqualTo("buy");
        then(orderDescription.getOrdertype()).isEqualTo("limit");
        then(orderDescription.getPrice()).isEqualTo(new BigDecimal("7230.4"));
    }

    @Test
    public void test_openOrders_when_includeTradeIsTrue_then_tradesRetrieved() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenOpenOrderResultDto> connectorResponse =
                krakenPrivateApiConnectorImpl.openOrders(false);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult().getOpen()).isNotNull();
        final KrakenOpenOrderDto openOrder = connectorResponse.getResult().getOpen().get("XXXXXX-YYYY5-1234ZZ");
        then(openOrder.getTrades()).containsExactly("AAAAAA-YY7ZZ-ABCD44");
    }

    @Test
    public void test_openOrders_when_twoRequests_then_bothRequestsDeserialized() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenOpenOrderResultDto> connectorResponse =
                krakenPrivateApiConnectorImpl.openOrders(false);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.OPEN_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        then(connectorResponse.getResult().getOpen()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull() //
                .containsOnlyKeys("AAAABB-YY7YY-4ZZZZZ", "BBBBCC-YY7YY-4ZZZZZ");
    }

    @Test
    public void test_closedOrders_when_includeTradeIsFalse_then_noTradesRetrieved() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenClosedOrderResultDto> connectorResponse =
                krakenPrivateApiConnectorImpl.closedOrders(false, 1583703494L);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap());

        then(connectorResponse.getError()).isEmpty();
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        then(connectorResponse.getResult().getCount()).isEqualTo(1);
        then(connectorResponse.getResult().getClosed()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull()
                .containsOnlyKeys("XXXXXX-YYYY5-ZZZZZZ");
        final KrakenClosedOrderDto closedOrder = connectorResponse.getResult().getClosed().get("XXXXXX-YYYY5-ZZZZZZ");
        then(closedOrder.getStatus()).isEqualTo("closed");
        then(closedOrder.getOpentm()).isEqualTo(new BigDecimal("1583703494.1067"));
        then(closedOrder.getClosetm()).isEqualTo(new BigDecimal("1583704373.4438"));
        then(closedOrder.getExpiretm()).isEqualTo(new BigDecimal("1583833094"));
        then(closedOrder.getVol()).isEqualTo(new BigDecimal("0.012345"));
        then(closedOrder.getVol_exec()).isEqualTo(new BigDecimal("0.012345"));
        then(closedOrder.getPrice()).isEqualTo(new BigDecimal("7230.3"));
        then(closedOrder.getOflags()).isEqualTo("fciq");
        then(closedOrder.getTrades()).isNull();
        final KrakenOrderDescriptionDto orderDescription = closedOrder.getDescr();
        then(orderDescription).isNotNull();
        then(orderDescription.getPair()).isEqualTo("XBTEUR");
        then(orderDescription.getType()).isEqualTo("buy");
        then(orderDescription.getOrdertype()).isEqualTo("limit");
        then(orderDescription.getPrice()).isEqualTo(new BigDecimal("7230.4"));
    }

    @Test
    public void test_closedOrders_when_includeTradeIsTrue_then_tradesRetrieved() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenClosedOrderResultDto> connectorResponse =
                krakenPrivateApiConnectorImpl.closedOrders(false, 1583703494L);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult().getClosed()).isNotNull();
        final KrakenClosedOrderDto closedOrder = connectorResponse.getResult().getClosed().get("XXXXXX-YYYY5-121Z44");
        then(closedOrder.getTrades()).containsExactly("AAAAAA-YY7YY-4ZZZZZ");
    }

    @Test
    public void test_closedOrders_when_twoRequests_then_bothRequestsDeserialized() throws Exception {

        // Given
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
        given(krakenApi.queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap())).willReturn(krakenApiResponse);

        // When
        final KrakenResponseDto<KrakenClosedOrderResultDto> connectorResponse =
                krakenPrivateApiConnectorImpl.closedOrders(false, 1583703494L);

        // Then
        verify(krakenApi).queryPrivate(eq(KrakenApi.Method.CLOSED_ORDERS), anyMap());

        // Asserts to avoid warnings caused by presence of @Nullable.
        then(connectorResponse.getResult()).isNotNull();
        then(connectorResponse.getResult().getClosed()) //
                // Asserts to avoid warnings caused by presence of @Nullable.
                .isNotNull() //
                .containsOnlyKeys("AAAAAA-YY7YY-4ZZZZZ", "BBBBBB-YY7YY-4ZZZZZ");
        then(connectorResponse.getResult().getCount()).isEqualTo(2);
    }
}
