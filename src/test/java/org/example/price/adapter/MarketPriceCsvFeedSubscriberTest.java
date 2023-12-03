package org.example.price.adapter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.example.price.application.AdjustedPricePublisher;
import org.example.price.application.MarketPrice;
import org.example.price.application.MarketPriceHandlerUseCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// this is an integration test and in a real project these probably should be put in a separate source set from unit tests
class MarketPriceCsvFeedSubscriberTest {

    @Test
    void adjustedPricesArePublishedWhenMessageIsConsumed() {
        // given
        String message = """
            345, GBP/USD, 1.25, 1.26, 01-12-2023 12:01:02:003
            346, EUR/JPY, 118, 119, 01-12-2023 12:01:02:099
        """;
        InMemoryAdjustedPricePublisher inMemoryAdjustedPricePublisher = new InMemoryAdjustedPricePublisher();
        MarketPriceCsvFeedSubscriber subject = new MarketPriceCsvFeedSubscriber(
            new MarketPriceHandlerUseCase(inMemoryAdjustedPricePublisher)
        );

        // when
        subject.onMessage(message);

        // then
        MarketPrice priceForGbpUsd =
            inMemoryAdjustedPricePublisher.getLast(new MarketPrice.Currency("GBP"), new MarketPrice.Currency("USD"));
        assertEquals("345", priceForGbpUsd.id());
        assertEquals("GBP", priceForGbpUsd.baseCurrency().symbol());
        assertEquals("USD", priceForGbpUsd.quoteCurrency().symbol());
        assertEquals(new BigDecimal("1.24875"), priceForGbpUsd.bidPrice());
        assertEquals(new BigDecimal("1.26126"), priceForGbpUsd.askPrice());
        assertEquals(LocalDateTime.parse("2023-12-01T12:01:02.003"), priceForGbpUsd.timestamp());

        MarketPrice priceForEurJpy =
            inMemoryAdjustedPricePublisher.getLast(new MarketPrice.Currency("EUR"), new MarketPrice.Currency("JPY"));
        assertEquals("346", priceForEurJpy.id());
        assertEquals("EUR", priceForEurJpy.baseCurrency().symbol());
        assertEquals("JPY", priceForEurJpy.quoteCurrency().symbol());
        assertEquals(new BigDecimal("117.882"), priceForEurJpy.bidPrice());
        assertEquals(new BigDecimal("119.119"), priceForEurJpy.askPrice());
        assertEquals(LocalDateTime.parse("2023-12-01T12:01:02.099"), priceForEurJpy.timestamp());
    }

    private static class InMemoryAdjustedPricePublisher implements AdjustedPricePublisher {

        private final Map<ImmutablePair<MarketPrice.Currency, MarketPrice.Currency>, MarketPrice> lastPublishedPrices =
            new HashMap<>();

        @Override
        public void publish(final MarketPrice marketPrice) {
            lastPublishedPrices.put(
                ImmutablePair.of(marketPrice.baseCurrency(), marketPrice.quoteCurrency()),
                marketPrice
            ) ;
        }

        private MarketPrice getLast(final MarketPrice.Currency baseCurrency, final MarketPrice.Currency quoteCurrency) {
            return lastPublishedPrices.get(ImmutablePair.of(baseCurrency, quoteCurrency));
        }
    }
}
