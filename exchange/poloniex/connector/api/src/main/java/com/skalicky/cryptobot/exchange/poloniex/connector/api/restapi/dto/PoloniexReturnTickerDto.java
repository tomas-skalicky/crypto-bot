package com.skalicky.cryptobot.exchange.poloniex.connector.api.restapi.dto;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * <pre>
 * {
 *     "id": 121,
 *     "last": "7988.65608131",
 *     "lowestAsk": "7989.32961488",
 *     "highestBid": "7988.66780100",
 *     "percentChange": "0.03206780",
 *     "baseVolume": "21888460.33185739",
 *     "quoteVolume": "2807.18940096",
 *     "isFrozen": "0",
 *     "high24hr": "7989.32961488",
 *     "low24hr": "7666.48331669"
 * }
 * </pre>
 */
public class PoloniexReturnTickerDto {
    @Nullable
    private Integer id;
    @Nullable
    private BigDecimal last;
    @Nullable
    private BigDecimal lowestAsk;
    @Nullable
    private BigDecimal highestBid;
    @Nullable
    private BigDecimal percentChange;
    @Nullable
    private BigDecimal baseVolume;
    @Nullable
    private BigDecimal quoteVolume;
    @Nullable
    private Boolean isFrozen;
    @Nullable
    private BigDecimal high24hr;
    @Nullable
    private BigDecimal low24hr;

    @Nullable
    public Integer getId() {
        return id;
    }

    public void setId(@Nullable final Integer id) {
        this.id = id;
    }

    @Nullable
    public BigDecimal getLast() {
        return last;
    }

    public void setLast(@Nullable final BigDecimal last) {
        this.last = last;
    }

    @Nullable
    public BigDecimal getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(@Nullable final BigDecimal lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    @Nullable
    public BigDecimal getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(@Nullable final BigDecimal highestBid) {
        this.highestBid = highestBid;
    }

    @Nullable
    public BigDecimal getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(@Nullable final BigDecimal percentChange) {
        this.percentChange = percentChange;
    }

    @Nullable
    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(@Nullable final BigDecimal baseVolume) {
        this.baseVolume = baseVolume;
    }

    @Nullable
    public BigDecimal getQuoteVolume() {
        return quoteVolume;
    }

    public void setQuoteVolume(@Nullable final BigDecimal quoteVolume) {
        this.quoteVolume = quoteVolume;
    }

    @Nullable
    public Boolean getFrozen() {
        return isFrozen;
    }

    public void setFrozen(@Nullable final Boolean frozen) {
        isFrozen = frozen;
    }

    @Nullable
    public BigDecimal getHigh24hr() {
        return high24hr;
    }

    public void setHigh24hr(@Nullable final BigDecimal high24hr) {
        this.high24hr = high24hr;
    }

    @Nullable
    public BigDecimal getLow24hr() {
        return low24hr;
    }

    public void setLow24hr(@Nullable final BigDecimal low24hr) {
        this.low24hr = low24hr;
    }
}
