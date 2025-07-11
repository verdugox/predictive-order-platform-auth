package com.webinarnttdata.auth.application.services;

import com.webinarnttdata.auth.adapters.out.client.UserClient;
import com.webinarnttdata.auth.domain.User;
import com.webinarnttdata.auth.domain.UserDTO;
import com.webinarnttdata.auth.domain.UserMapper;
import com.webinarnttdata.auth.domain.patterns.*;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class AuthFacade {

    @Inject
    UserService userService;
    @Inject
    UserClient userClient;// Inyección del adapter (Circuit Breaker listo)

    public String simulatePredictionWithCircuitBreaker() {
        // Llamada al metodo del cliente que simula la llamada al Prediction Service
        return userClient.simulatePredictionServiceCall();
    }

    //Metodo para registrar usuarios(ya integrado con MongoDB)
    public Uni<Response> registerUser(UserDTO userDTO) {
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        return userService.register(user)
                .onItem().transform(u -> Response.ok(u).build());
    }

    //Metodo para simular llamada al Prediction Service usando Adapter
    public String callPrediction(){
        ExternalServiceAdapter adapter = new PredictionServiceAdapter();
        return adapter.fetchData();
    }


    public boolean validateUserByRole(User user) {
        UserValidator validator = new UserValidator(new RoleValidationStrategy());
        return validator.validate(user);
    }

    public boolean validateUserByUsername(User user) {
        UserValidator validator = new UserValidator(new UsernameValidationStrategy());
        return validator.validate(user);
    }



}
