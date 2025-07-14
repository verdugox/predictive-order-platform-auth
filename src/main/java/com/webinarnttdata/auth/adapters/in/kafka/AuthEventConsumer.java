package com.webinarnttdata.auth.adapters.in.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class AuthEventConsumer {

    @Incoming("auth-events-in")
    public void consume(String message) {
        System.out.println("Mensaje recibido en Kafka Consumer: " + message);
    }
}
