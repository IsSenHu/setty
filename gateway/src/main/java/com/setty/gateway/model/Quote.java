package com.setty.gateway.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;

/**
 * @author HuSen
 * create on 2019/7/25 11:43
 */
@Data
public class Quote {
    private static final MathContext MATH_CONTEXT = new MathContext(2);

    private String ticker;

    private BigDecimal price;

    private Instant instant;

    public Quote() {
    }

    public Quote(String ticker, BigDecimal price) {
        this.ticker = ticker;
        this.price = price;
    }

    public Quote(String ticker, Double price) {
        this(ticker, new BigDecimal(price, MATH_CONTEXT));
    }
}
