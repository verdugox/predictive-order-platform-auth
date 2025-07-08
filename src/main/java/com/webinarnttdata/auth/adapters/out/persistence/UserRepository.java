package com.webinarnttdata.auth.adapters.out.persistence;

import com.webinarnttdata.auth.domain.User;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements ReactivePanacheMongoRepository<User> {
}
