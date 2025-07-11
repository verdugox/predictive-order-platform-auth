package com.webinarnttdata.auth.domain.patterns;

import com.webinarnttdata.auth.domain.User;

public interface UserValidationStrategy {

    boolean validate(User user);

}
