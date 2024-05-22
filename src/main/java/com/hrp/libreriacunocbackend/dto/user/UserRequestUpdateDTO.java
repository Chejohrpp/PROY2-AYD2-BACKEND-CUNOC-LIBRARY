package com.hrp.libreriacunocbackend.dto.user;

import lombok.Value;

@Value
public class UserRequestUpdateDTO {
    Long id;
    String CurrentPassword;
    String newPassword;
    String username;
}
