package com.webinarnttdata.auth.adapters.in.rest;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;

@Path("/cache")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RedisResource {

    @Inject
    RedisClient redisClient;

    @POST
    @Path("/message/{key}")
    @Counted(value = "cache.save.count", description = "Número de mensajes guardados en cache")
    @Timed(value = "cache.save.timer", description = "Tiempo en guardar un mensaje en cache")
    public jakarta.ws.rs.core.Response saveMessage(@PathParam("key") String key, String message) {
        redisClient.set(List.of(key, message));
        return jakarta.ws.rs.core.Response.status(Status.CREATED)
                .entity("Message saved with key: " + key)
                .build();
    }

    @GET
    @Path("/message/{key}")
    @Counted(value = "cache.get.count", description = "Número de mensajes consultados en cache")
    @Timed(value = "cache.get.timer", description = "Tiempo en obtener un mensaje del cache")
    public jakarta.ws.rs.core.Response getMessage(@PathParam("key") String key) {
        Response response = redisClient.get(key);
        if (response == null) {
            return jakarta.ws.rs.core.Response.status(Status.NOT_FOUND)
                    .entity("No message found for key: " + key)
                    .build();
        }
        return jakarta.ws.rs.core.Response.ok(response.toString()).build();
    }
}