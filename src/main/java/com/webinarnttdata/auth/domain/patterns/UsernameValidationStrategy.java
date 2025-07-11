package com.webinarnttdata.auth.domain.patterns;

import com.webinarnttdata.auth.domain.User;

public class UsernameValidationStrategy implements UserValidationStrategy {
    @Override
    public boolean validate(User user) {
        return user.getUsername() != null && user.getUsername().startsWith("admin");
    }
}
