package com.setty.gateway.generator;

import com.setty.gateway.model.Quote;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author HuSen
 * create on 2019/7/25 11:44
 */
@Component
public class QuoteGenerator {

    private final MathContext mathContext = new MathContext(2);

    private final Random random = new Random();

    private final List<Quote> prices = new ArrayList<>();

    /**
     * 生成行情数据
     */
    public QuoteGenerator() {
        this.prices.add(new Quote("CTXS", 82.26));
        this.prices.add(new Quote("DELL", 63.74));
        this.prices.add(new Quote("GOOG", 847.24));
        this.prices.add(new Quote("MSFT", 65.11));
        this.prices.add(new Quote("ORCL", 45.71));
        this.prices.add(new Quote("RHT", 84.29));
        this.prices.add(new Quote("VMW", 92.21));
    }

    public Flux<Quote> fetchQuoteStream(Duration period) {
        return Flux.interval(period)
                .onBackpressureDrop()
                .map(this::generateQuotes)
                .flatMapIterable(quotes -> quotes)
                .log("io.spring.workshop.stockquotes");
    }

    private List<Quote> generateQuotes(long interval) {
        final Instant instant = Instant.now();
        return prices.stream()
                .map(baseQuote -> {
                    BigDecimal priceChange = baseQuote.getPrice().multiply(new BigDecimal(0.05 * this.random.nextDouble()), this.mathContext);
                    Quote result = new Quote(baseQuote.getTicker(), baseQuote.getPrice().add(priceChange));
                    result.setInstant(instant);
                    return result;
                }).collect(Collectors.toList());
    }
}
