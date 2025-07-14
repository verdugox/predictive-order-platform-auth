package com.webinarnttdata.auth.adapters.in.rest;
import io.quarkus.redis.client.RedisClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/redis")
public class RedisResource {

    @Inject
    RedisClient redisClient;

    @GET
    public String testRedis() {
        redisClient.set(List.of("test-key", "hello"));
        return redisClient.get("test-key").toString();
    }
}