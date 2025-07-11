package com.webinarnttdata.auth.domain.patterns;

import com.webinarnttdata.auth.domain.User;

public class UserValidator {
    private final UserValidationStrategy strategy;

    public UserValidator(UserValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean validate(User user) {
        return strategy.validate(user);
    }
}
