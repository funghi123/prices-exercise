package org.example.price.adapter;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.example.price.application.MarketPrice;
import org.example.price.application.MarketPriceHandlerUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class MarketPriceCsvFeedSubscriber {

    private static final Logger log = LoggerFactory.getLogger(MarketPriceCsvFeedSubscriber.class);
    private static final String MARKET_PRICE_MESSAGE_TIMESTAMP_FORMAT = "dd-MM-yyyy HH:mm:ss:SSS";
    private static final String CURRENCY_SEPARATOR = "/";

    private final MarketPriceHandlerUseCase marketPriceHandlerUseCase;

    void onMessage(final String message) {
        // in real world probably we should log some more data and maybe lower level (debug)
        log.info(String.format("Incoming message %s", message));
        parseMessage(message).forEach(marketPriceHandlerUseCase::handleIncomingQuotation);
    }

    private List<MarketPrice> parseMessage(final String message) {
        // jackson-dataformat-csv could be used here, but it turned out that it was faster to code on my own
        return Arrays.stream(message.split("\n")).map(this::parseLine).toList();
    }

    private MarketPrice parseLine(final String line) {
        String[] fields = Arrays.stream(line.split(",")).map(String::trim).toArray(String[]::new);
        assert fields.length == 5;
        ImmutablePair<MarketPrice.Currency, MarketPrice.Currency> currencies = parseCurrencyPair(fields[1]);
        return new MarketPrice(
            fields[0],
            currencies.getLeft(),
            currencies.getRight(),
            new BigDecimal(fields[2]),
            new BigDecimal(fields[3]),
            LocalDateTime.parse(fields[4], DateTimeFormatter.ofPattern(MARKET_PRICE_MESSAGE_TIMESTAMP_FORMAT))
        );
    }

    private static ImmutablePair<MarketPrice.Currency, MarketPrice.Currency> parseCurrencyPair(String currencyPair) {
        String[] symbols = currencyPair.split(CURRENCY_SEPARATOR);
        assert symbols.length == 2;
        return ImmutablePair.of(new MarketPrice.Currency(symbols[0]), new MarketPrice.Currency(symbols[1]));
    }
}
