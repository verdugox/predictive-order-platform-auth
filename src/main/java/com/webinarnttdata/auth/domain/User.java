package com.webinarnttdata.auth.domain;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;
public class User extends ReactivePanacheMongoEntity {

    @BsonProperty("username")
    public String username;

    @BsonProperty("password")
    public String password;

    @BsonProperty("role")
    public String role;

}
