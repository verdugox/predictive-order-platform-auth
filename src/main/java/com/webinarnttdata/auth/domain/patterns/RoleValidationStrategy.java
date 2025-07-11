package com.webinarnttdata.auth.domain.patterns;

import com.webinarnttdata.auth.domain.User;

public class RoleValidationStrategy implements UserValidationStrategy{

    @Override
    public boolean validate(User user) {
        // Aquí iría la lógica para validar el rol del usuario
        // Por ejemplo, verificar si el rol es "ADMIN" o "USER"
        return "admin".equals(user.getRole()) || "USER".equals(user.getRole());
    }

}
