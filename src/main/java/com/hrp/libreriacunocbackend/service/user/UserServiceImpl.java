package com.hrp.libreriacunocbackend.service.user;

import com.hrp.libreriacunocbackend.dto.librarian.LibrarianRequestDTO;
import com.hrp.libreriacunocbackend.dto.librarian.LibrarianResponseDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestDTO;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(UserRequestDTO userRequestDTO) throws NotAcceptableException {
        Optional<User> foundUser = userRepository.findByUsername(userRequestDTO.getUsername());
        if (foundUser.isPresent()) {
            throw new NotAcceptableException("USERNAME REPETIDO");
        }

        User newUser = new User();
        newUser.setUsername(userRequestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        newUser.setRole(userRequestDTO.getRole());

        newUser  = userRepository.save(newUser);
        return newUser;
    }

    @Override
    public LibrarianResponseDTO createLibrarian(LibrarianRequestDTO librarianRequestDTO) throws NotAcceptableException {
        UserRequestDTO userRequestDTO = new UserRequestDTO(librarianRequestDTO);
        User newLibrarian = createUser(userRequestDTO);
        return new LibrarianResponseDTO(newLibrarian);
    }

    @Override
    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User editUser(UserRequestDTO userRequestDTO) throws NotAcceptableException {
        return null;
    }
}
