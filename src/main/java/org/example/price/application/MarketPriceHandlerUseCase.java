package org.example.price.application;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MarketPriceHandlerUseCase {

    private final AdjustedPricePublisher adjustedPricePublisher;

    public void handleIncomingQuotation(MarketPrice marketPrice) {
        // assumed published prices have the same definition as received ones
        adjustedPricePublisher.publish(marketPrice.adjust());
    }

}