package com.webinarnttdata.auth.adapters.out.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Supplier;

@ApplicationScoped
public class UserClient {

    private static final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("userClient");
    private static final Retry retry = Retry.ofDefaults("userClient");

    public String simulatePredictionServiceCall() {
        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    throw new RuntimeException("Servicio Prediction falla (simulado)");
                });

        Supplier<String> retrySupplier = Retry
                .decorateSupplier(retry, decoratedSupplier);

        return Try.ofSupplier(retrySupplier)
                .recover(throwable -> "Fallback: Servicio Prediction no disponible")
                .get();
    }
}
