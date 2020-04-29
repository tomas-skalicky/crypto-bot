package com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.logic;

import com.google.common.collect.ImmutableMap;
import com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.dto.PoloniexReturnTickerDto;

import javax.annotation.Nonnull;

public interface PoloniexPublicApiConnector {

    /**
     * <pre>
     * {
     *     ...
     *     "BTC_XRP": {
     *         "id": 117,
     *         "last": "0.00002693",
     *         "lowestAsk": "0.00002692",
     *         "highestBid": "0.00002691",
     *         "percentChange": "0.01891789",
     *         "baseVolume": "490.97365508",
     *         "quoteVolume": "18028762.12187298",
     *         "isFrozen": "0",
     *         "high24hr": "0.00002807",
     *         "low24hr": "0.00002641"
     *         },
     *     "USDT_BTC": {
     *         "id": 121,
     *         "last": "7988.65608131",
     *         "lowestAsk": "7989.32961488",
     *         "highestBid": "7988.66780100",
     *         "percentChange": "0.03206780",
     *         "baseVolume": "21888460.33185739",
     *         "quoteVolume": "2807.18940096",
     *         "isFrozen": "0",
     *         "high24hr": "7989.32961488",
     *         "low24hr": "7666.48331669"
     *     },
     *     ...
     * }
     * </pre>
     */
    @Nonnull
    ImmutableMap<String, PoloniexReturnTickerDto> returnTicker();

}
