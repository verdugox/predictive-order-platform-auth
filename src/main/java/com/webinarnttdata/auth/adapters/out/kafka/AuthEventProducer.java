package com.webinarnttdata.auth.adapters.out.kafka;

import io.smallrye.reactive.messaging.annotations.Broadcast;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

//Codigo del Topico Producer
@ApplicationScoped
public class AuthEventProducer {

    @Channel("auth-events-out")
    Emitter<String> emitter;

    public void sendEvent(String message) {
        System.out.println("Enviando mensaje a Kafka: " + message);
        emitter.send(message);
    }
}
