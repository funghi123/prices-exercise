package org.example.price.application;

import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MarketPrice(

    @NonNull
    String id,
    @NonNull
    Currency baseCurrency,
    @NonNull
    Currency quoteCurrency,
    @NonNull
    BigDecimal bidPrice,
    @NonNull
    BigDecimal askPrice,
    @NonNull
    LocalDateTime timestamp // I assumed time zone information is not needed
) {

    // assuming the same value for bid & ask
    private static final BigDecimal MARGIN = new BigDecimal("0.001");

    MarketPrice adjust() {
        return new MarketPrice(
            id,
            baseCurrency,
            quoteCurrency,
            bidPrice.subtract(calculateMargin(bidPrice)),
            askPrice.add(calculateMargin(askPrice)),
            timestamp
        );
    }

    private static BigDecimal calculateMargin(BigDecimal price) {
        return price.multiply(MARGIN);
    }

    public record Currency(
        @NonNull
        String symbol // this could be probably an enum, but I cannot assume it from given description
    ) {}
}