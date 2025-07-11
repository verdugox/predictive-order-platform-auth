package com.webinarnttdata.auth.adapters.in.rest;

import com.webinarnttdata.auth.application.services.UserService;
import com.webinarnttdata.auth.domain.User;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class UserResourceTest {

    @InjectMock
    UserService userService;

    @Test
    @DisplayName("Test Register User")
    void testRegisterUser() {
        User user = new User();
        user.username  = "admin";
        user.password = "admin123";
        user.role = "ADMIN";
        when(userService.register(any(User.class)))
                .thenReturn(Uni.createFrom().item(user));
        UserResource resource = new UserResource();
        resource.userService = userService;
        Response response = resource.register(user).await().indefinitely();
        assertEquals(200, response.getStatus());
        User result = (User) response.getEntity();
        assertEquals("admin", result.username);

    }

}
