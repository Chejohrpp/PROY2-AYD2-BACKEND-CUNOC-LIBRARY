package com.hrp.libreriacunocbackend.dto.user;

import com.hrp.libreriacunocbackend.dto.librarian.LibrarianRequestDTO;
import com.hrp.libreriacunocbackend.dto.student.StudentRequestDTO;
import com.hrp.libreriacunocbackend.enums.user.Role;
import lombok.Value;

@Value
public class UserRequestDTO {
    String username;
    String password;
    Role role;

    public UserRequestDTO(StudentRequestDTO studentRequestDTO) {
        this.username = studentRequestDTO.getUsername();
        this.password = studentRequestDTO.getCarnet();
        this.role = Role.STUDENT;
    }

    public UserRequestDTO(LibrarianRequestDTO librarianRequestDTO){
        this.username = librarianRequestDTO.getUsername();
        this.password = librarianRequestDTO.getPassword();
        this.role = Role.LIBRARIAN;
    }
}
