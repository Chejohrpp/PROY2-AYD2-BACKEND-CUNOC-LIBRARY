package com.hrp.libreriacunocbackend.dto.librarian;

import com.hrp.libreriacunocbackend.entities.user.User;
import lombok.Value;

@Value
public class LibrarianResponseDTO {
    Long userId;
    String userName;
    String password;

    public LibrarianResponseDTO(User user){
        this.userId = user.getIdUser();
        this.userName = user.getUsername();
        this.password = user.getPassword();
    }
}
