package com.webinarnttdata.auth.application.services;

import com.webinarnttdata.auth.adapters.out.persistence.UserRepository;
import com.webinarnttdata.auth.domain.User;
import com.webinarnttdata.auth.domain.patterns.AppLogger;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    private static final Logger logger = AppLogger.getInstance();

    public Uni<User> register(User user){
        logger.info(AppLogger.colorInfo("Registrando usuario: " + user.username));
        return userRepository.persist(user)
                .invoke(u -> logger.info(AppLogger.colorInfo("Usuario registrado exitosamente: " + u.username)));
    }
    public Uni<User> findByUsername(String username) {
        logger.info(AppLogger.colorInfo("Buscando usuario por username: " + username));
        return userRepository.find("username", username).firstResult()
                .invoke(u -> {;
                    if (u != null) {
                        logger.info(AppLogger.colorInfo("Usuario encontrado: " + u.username));
                    } else {
                        logger.warn(AppLogger.colorWarning("Usuario no encontrado con username: " + username));
                    }
                });
    }

}
