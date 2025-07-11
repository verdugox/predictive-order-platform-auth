package com.webinarnttdata.auth.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    public String username;
    public String password;
    public String role;

}
