package com.hrp.libreriacunocbackend.service.user;

import com.hrp.libreriacunocbackend.dto.librarian.LibrarianRequestDTO;
import com.hrp.libreriacunocbackend.dto.librarian.LibrarianResponseDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestUpdateDTO;
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
        // Validar el username
        if (librarianRequestDTO.getUsername() == null || librarianRequestDTO.getUsername().trim().isEmpty()) {
            throw new NotAcceptableException("El nombre de usuario no puede estar vacío.");
        }
        if (librarianRequestDTO.getUsername().length() < 4 || librarianRequestDTO.getUsername().length() > 20) {
            throw new NotAcceptableException("El nombre de usuario debe tener entre 4 y 20 caracteres.");
        }
        // Validar el password
        if (librarianRequestDTO.getPassword() == null || librarianRequestDTO.getPassword().trim().isEmpty()) {
            throw new NotAcceptableException("La contraseña no puede estar vacía.");
        }
        if (librarianRequestDTO.getPassword().length() < 4) {
            throw new NotAcceptableException("La contraseña debe tener al menos 4 caracteres.");
        }

        // Crear el usuario
        UserRequestDTO userRequestDTO = new UserRequestDTO(librarianRequestDTO);
        User newLibrarian = createUser(userRequestDTO);
        return new LibrarianResponseDTO(newLibrarian);
    }


    @Override
    public Optional<User> findUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User editUser(UserRequestUpdateDTO userRequestUpdateDTO) throws NotAcceptableException {
        // Buscar el usuario existente por su ID
        Optional<User> foundUserOptional = userRepository.findById(userRequestUpdateDTO.getId());
        if (!foundUserOptional.isPresent()) {
            throw new NotAcceptableException("El usuario no existe.");
        }

        User existingUser = foundUserOptional.get();

        // Validar el currentPassword
        if (!passwordEncoder.matches(userRequestUpdateDTO.getCurrentPassword(), existingUser.getPassword())) {
            throw new NotAcceptableException("La contraseña actual es incorrecta.");
        }

        // Validar el nuevo username
        if (!existingUser.getUsername().equals(userRequestUpdateDTO.getUsername())) {
            Optional<User> foundUserByUsername = userRepository.findByUsername(userRequestUpdateDTO.getUsername());
            if (foundUserByUsername.isPresent()) {
                throw new NotAcceptableException("USERNAME REPETIDO");
            }
            existingUser.setUsername(userRequestUpdateDTO.getUsername());
        }

        // Codificar la nueva contraseña si está presente
        if (userRequestUpdateDTO.getNewPassword() != null && !userRequestUpdateDTO.getNewPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userRequestUpdateDTO.getNewPassword()));
        }

        // Guardar el usuario actualizado
        User updatedUser = userRepository.save(existingUser);
        return updatedUser;
    }


}
