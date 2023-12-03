package org.example.price.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketPriceTest {

    @Test
    void adjustingPriceWorks() {
        // given
        MarketPrice marketPrice = new MarketPrice(
            "123",
            new MarketPrice.Currency("EUR"),
            new MarketPrice.Currency("USD"),
            new BigDecimal("1.1"),
            new BigDecimal("1.2"),
            LocalDateTime.parse("2023-12-03T23:01")
        );

        // when
        MarketPrice result = marketPrice.adjust();

        // then
        assertEquals("123", result.id());
        assertEquals("EUR", result.baseCurrency().symbol());
        assertEquals("USD", result.quoteCurrency().symbol());
        assertEquals(new BigDecimal("1.0989"), result.bidPrice());
        assertEquals(new BigDecimal("1.2012"), result.askPrice());
        assertEquals(LocalDateTime.parse("2023-12-03T23:01"), result.timestamp());
    }
}
