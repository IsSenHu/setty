package com.setty.gateway.handler;

import com.setty.gateway.generator.QuoteGenerator;
import com.setty.gateway.model.Quote;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.time.Duration.ofMillis;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @author HuSen
 * create on 2019/7/25 11:52
 */
@Component
public class QuoteHandler {

    private final Flux<Quote> quoteStream;

    public QuoteHandler(QuoteGenerator quoteGenerator) {
        this.quoteStream = quoteGenerator.fetchQuoteStream(ofMillis(1000)).share();
    }

    @NonNull
    public Mono<ServerResponse> hello(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromObject("Hello Spring!"));
    }

    @NonNull
    public Mono<ServerResponse> echo(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .body(request.bodyToMono(String.class), String.class);
    }

    @NonNull
    public Mono<ServerResponse> streamQuotes(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(this.quoteStream, Quote.class);
    }

    @NonNull
    public Mono<ServerResponse> fetchQuotes(ServerRequest request) {
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(this.quoteStream.take(size), Quote.class);
    }
}
