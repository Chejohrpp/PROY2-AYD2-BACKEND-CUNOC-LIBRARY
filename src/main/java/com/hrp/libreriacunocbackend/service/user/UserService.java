package com.hrp.libreriacunocbackend.service.user;

import com.hrp.libreriacunocbackend.dto.librarian.LibrarianRequestDTO;
import com.hrp.libreriacunocbackend.dto.librarian.LibrarianResponseDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestDTO;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;

public interface UserService {
//    List<UserResponseDTO> findAll();

    User createUser(UserRequestDTO userRequestDTO) throws NotAcceptableException;

    LibrarianResponseDTO createLibrarian(LibrarianRequestDTO librarianRequestDTO) throws NotAcceptableException;

    User editUser(UserRequestDTO userRequestDTO) throws NotAcceptableException;
}
