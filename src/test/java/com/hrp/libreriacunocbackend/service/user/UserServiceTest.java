package com.hrp.libreriacunocbackend.service.user;

import com.hrp.libreriacunocbackend.dto.librarian.LibrarianRequestDTO;
import com.hrp.libreriacunocbackend.dto.librarian.LibrarianResponseDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestDTO;
import com.hrp.libreriacunocbackend.dto.user.UserRequestUpdateDTO;
import com.hrp.libreriacunocbackend.entities.user.User;
import com.hrp.libreriacunocbackend.enums.user.Role;
import com.hrp.libreriacunocbackend.exceptions.NotAcceptableException;
import com.hrp.libreriacunocbackend.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    }

    @Test
    void createUser(){
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("lib0test", "test");
        UserRequestDTO userRequestDTO = new UserRequestDTO(librarianRequestDTO);
        when(userRepository.save(any(User.class))).thenReturn(new User());
        User user = null;
        try {
            user = userService.createUser(userRequestDTO);
        } catch (NotAcceptableException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(user);
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void createLibrarian_usernameIsNull() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO(null, "password123");

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("El nombre de usuario no puede estar vacío.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createLibrarian_usernameIsEmpty() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO(" ", "password123");

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("El nombre de usuario no puede estar vacío.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createLibrarian_usernameTooShort() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("abc", "password123");

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("El nombre de usuario debe tener entre 4 y 20 caracteres.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createLibrarian_usernameTooLong() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("a".repeat(21), "password123");

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("El nombre de usuario debe tener entre 4 y 20 caracteres.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createLibrarian_passwordIsNull() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("username123", null);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("La contraseña no puede estar vacía.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createLibrarian_passwordIsEmpty() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("username123", " ");

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("La contraseña no puede estar vacía.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }


    @Test
    void createLibrarian_passwordTooShort() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("username123", "123");

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("La contraseña debe tener al menos 4 caracteres.", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createLibrarian_usernameAlreadyExists() {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("username123", "password123");

        when(userRepository.findByUsername("username123")).thenReturn(Optional.of(new User()));

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.createLibrarian(librarianRequestDTO);
        });

        assertEquals("USERNAME REPETIDO", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("username123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createLibrarian_successfulCreation() throws NotAcceptableException {
        LibrarianRequestDTO librarianRequestDTO = new LibrarianRequestDTO("username123", "password123");

        when(userRepository.findByUsername("username123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LibrarianResponseDTO responseDTO = userService.createLibrarian(librarianRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("username123", responseDTO.getUserName());
        verify(userRepository, times(1)).findByUsername("username123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUser_userFound() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findUser(username);

        assertTrue(foundUser.isPresent());
        assertEquals(username, foundUser.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findUser_userNotFound() {
        String username = "nonexistentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.findUser(username);

        assertFalse(foundUser.isPresent());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void editUser_userNotFound() {
        UserRequestUpdateDTO userRequestUpdateDTO = new UserRequestUpdateDTO(1L, "currentPass", "newPass", "newUsername");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.editUser(userRequestUpdateDTO);
        });

        assertEquals("El usuario no existe.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editUser_incorrectCurrentPassword() {
        UserRequestUpdateDTO userRequestUpdateDTO = new UserRequestUpdateDTO(1L, "currentPass", "newPass", "newUsername");
        User existingUser = new User();
        existingUser.setPassword("encodedCurrentPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(false);

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.editUser(userRequestUpdateDTO);
        });

        assertEquals("La contraseña actual es incorrecta.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editUser_usernameAlreadyExists() {
        UserRequestUpdateDTO userRequestUpdateDTO = new UserRequestUpdateDTO(1L, "currentPass", "newPass", "newUsername");
        User existingUser = new User();
        existingUser.setIdUser(1L);
        existingUser.setUsername("currentUsername");
        existingUser.setPassword("encodedCurrentPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(true);
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.of(new User()));

        NotAcceptableException exception = assertThrows(NotAcceptableException.class, () -> {
            userService.editUser(userRequestUpdateDTO);
        });

        assertEquals("USERNAME REPETIDO", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void editUser_successfulUpdateWithUsernameAndPasswordChange() throws NotAcceptableException {
        UserRequestUpdateDTO userRequestUpdateDTO = new UserRequestUpdateDTO(1L, "currentPass", "newPass", "newUsername");
        User existingUser = new User();
        existingUser.setIdUser(1L);
        existingUser.setUsername("currentUsername");
        existingUser.setPassword("encodedCurrentPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(true);
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.editUser(userRequestUpdateDTO);

        assertNotNull(updatedUser);
        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("encodedNewPass", updatedUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void editUser_successfulUpdateWithUsernameChangeOnly() throws NotAcceptableException {
        UserRequestUpdateDTO userRequestUpdateDTO = new UserRequestUpdateDTO(1L, "currentPass", null, "newUsername");
        User existingUser = new User();
        existingUser.setIdUser(1L);
        existingUser.setUsername("currentUsername");
        existingUser.setPassword("encodedCurrentPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(true);
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.editUser(userRequestUpdateDTO);

        assertNotNull(updatedUser);
        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals("encodedCurrentPass", updatedUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void editUser_successfulUpdateWithPasswordChangeOnly() throws NotAcceptableException {
        UserRequestUpdateDTO userRequestUpdateDTO = new UserRequestUpdateDTO(1L, "currentPass", "newPass", "currentUsername");
        User existingUser = new User();
        existingUser.setIdUser(1L);
        existingUser.setUsername("currentUsername");
        existingUser.setPassword("encodedCurrentPass");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("currentPass", "encodedCurrentPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.editUser(userRequestUpdateDTO);

        assertNotNull(updatedUser);
        assertEquals("currentUsername", updatedUser.getUsername());
        assertEquals("encodedNewPass", updatedUser.getPassword());
        verify(userRepository, times(1)).save(existingUser);
    }



}