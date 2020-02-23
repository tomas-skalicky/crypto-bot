/*
 * A program to automatically trade cryptocurrencies.
 * Copyright (C) 2020 $user.name<$user.email>
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

package com.skalicky.cryptobot.exchange.kraken.facade.impl.logic;

import com.skalicky.cryptobot.exchange.kraken.connector.api.dto.KrakenResponseDto;
import com.skalicky.cryptobot.exchange.kraken.connector.api.logic.KrakenPublicApiConnector;
import com.skalicky.cryptobot.exchange.kraken.facade.api.bo.KrakenTickerPairBo;
import com.skalicky.cryptobot.exchange.kraken.facade.impl.converter.MapEntryToKrakenTickerPairBoConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KrakenPublicApiFacadeImplUTest {
    @Nonnull
    private final KrakenPublicApiConnector krakenPublicApiConnector = mock(KrakenPublicApiConnector.class);
    @Nonnull
    private final KrakenPublicApiFacadeImpl krakenPublicApiFacadeImpl = new KrakenPublicApiFacadeImpl(krakenPublicApiConnector,
            new MapEntryToKrakenTickerPairBoConverter());

    @AfterEach
    public void assertAndCleanMocks() {
        Mockito.verifyNoMoreInteractions(krakenPublicApiConnector);
        Mockito.reset(krakenPublicApiConnector);
    }

    @Test
    public void ticker_when_dataForPairInResponseFile_then_askPriceReturned_and_bidPriceReturned() throws Exception {

        final Map<String, Object> pairData = Map.of("a", List.of("8903.300000"), "b", List.of("8902.400000"));
        final Map<String, Map<String, Object>> result = Map.of("XXBTZEUR", pairData);
        final KrakenResponseDto<Map<String, Map<String, Object>>> expectedResponse = new KrakenResponseDto<>();
        expectedResponse.setResult(result);
        final String pairName = "XBTEUR";
        final List<String> pairNames = Collections.singletonList(pairName);
        when(krakenPublicApiConnector.ticker(pairNames)).thenReturn(expectedResponse);

        final List<KrakenTickerPairBo> response = krakenPublicApiFacadeImpl.ticker(pairName);

        verify(krakenPublicApiConnector).ticker(pairNames);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getPairName()).isEqualTo("XXBTZEUR");
        assertThat(response.get(0).getAskPrice().stripTrailingZeros()).isEqualTo(new BigDecimal("8903.3").stripTrailingZeros());
        assertThat(response.get(0).getBidPrice().stripTrailingZeros()).isEqualTo(new BigDecimal("8902.4").stripTrailingZeros());
    }
}
