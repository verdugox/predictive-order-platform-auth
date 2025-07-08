package com.webinarnttdata.auth.application;

import com.webinarnttdata.auth.adapters.out.persistence.UserRepository;
import com.webinarnttdata.auth.domain.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public Uni<User> register(User user){
        return userRepository.persist(user);
    }
    public Uni<User> findByUsername(String username) {
        return userRepository.find("username", username).firstResult();
    }

}
