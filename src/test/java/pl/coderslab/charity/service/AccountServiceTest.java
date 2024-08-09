package pl.coderslab.charity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import pl.coderslab.charity.dto.PasswordDTO;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.UserExistsException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private final Long ID = 1L;
    private final String FIRST_NAME = "Anakin";
    private final String EMAIL = "sky.guy@republic.co";
    private final char[] PASSWORD = {'p', 'a', 's', 's', 'w', 'o', 'r', 'd'};
    private final int ENABLED = 1;

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private EmailService mockEmailService;
    @Mock
    private BCryptPasswordEncoder mockPasswordEncoder;
    @Mock
    private SpringDataUserDetailsService mockSpringDataUserDetailsService;
    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldSaveNewUser() {
        // given
        UserDTO userDTO = new UserDTO(FIRST_NAME, EMAIL, ENABLED);
        PasswordDTO passwordDTO = new PasswordDTO(PASSWORD);

        User user = new User();
        user.setFirstName(FIRST_NAME);
        user.setEmail(EMAIL);
        user.setEnabled(ENABLED);
        user.setPassword("encodedPassword");
        Role role = new Role("ROLE_ADMIN");
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        when(mockRoleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(mockUserRepository.save(any(User.class))).thenReturn(user);
        when(mockPasswordEncoder.encode("password")).thenReturn("encodedPassword");

        // when
        User result = accountService.saveNewUser(userDTO, passwordDTO, "ADMIN");

        // then
        assertNotNull(result.getUuid());
        assertTrue(result.getRoles().contains(role));
        assertEquals(FIRST_NAME, result.getFirstName());
        assertEquals(EMAIL, result.getEmail());
    }


    @Test
    void saveNewUser_shouldThrowUserExistsException() {
        // given
        User user = new User(FIRST_NAME, EMAIL, "password", ENABLED);
        UserDTO userDTO = new UserDTO(FIRST_NAME, EMAIL, ENABLED);
        PasswordDTO passwordDTO = new PasswordDTO(PASSWORD);

        when(mockUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        UserExistsException thrown = assertThrows(UserExistsException.class, () -> accountService.saveNewUser(userDTO, passwordDTO, "ADMIN"));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " już istnieje."));
    }

    @Test
    void createAccount() {
        // given
        User user = new User(FIRST_NAME, EMAIL, "encodedPassword", ENABLED);
        user.setUuid(UUID.randomUUID());
        UserDTO userDTO = new UserDTO(FIRST_NAME, EMAIL, ENABLED);
        PasswordDTO passwordDTO = new PasswordDTO(PASSWORD);
        Role role = new Role("ROLE_USER");
        String activationLink = "http://localhost:8080/activation/" + user.getUuid();
        String emailText = "Cześć " + FIRST_NAME + ", oto link aktywacyjny do Twojego konta: " + activationLink + " - kliknij, aby aktywować konto i móc się zalogować.";

        // Mockowanie
        when(mockRoleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(mockUserRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(mockEmailService).sendSimpleMessage(EMAIL, "Account confirmation", emailText);

        // when
        String result = accountService.createAccount(userDTO, passwordDTO);

        // then
        assertEquals("Udało się! Zajrzyj na swojego maila (" + EMAIL + "), tam znajdziesz link aktywacyjny do Twojego konta.", result);
        verify(mockEmailService).sendSimpleMessage(EMAIL, "Account confirmation", emailText); // Sprawdzenie wywołania metody
    }


    @Test
    void createAccount_shouldThrowUserExistsException() {
        // given
        User user = new User(FIRST_NAME, EMAIL, "password", ENABLED);
        UserDTO userDTO = new UserDTO(FIRST_NAME, EMAIL, ENABLED);
        PasswordDTO passwordDTO = new PasswordDTO(PASSWORD);
        when(mockUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        UserExistsException thrown = assertThrows(UserExistsException.class, () -> accountService.createAccount(userDTO, passwordDTO));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " już istnieje."));
    }

    @Test
    void comparePasswords_shouldPass() {
        // given + when
        boolean result = accountService.comparePasswords(PASSWORD, PASSWORD);

        // then
        assertTrue(result);
    }

    @Test
    void comparePasswords_shouldNotPass() {
        // given + when
        char[] newPassword = {'X', 'X', 'X'};
        boolean result = accountService.comparePasswords(PASSWORD, newPassword);

        // then
        assertFalse(result);
    }

    @Test
    void shouldUpdatePassword() {
        // given
        char[] newPassword = {'N', 'E', 'W'};
        User user = new User(FIRST_NAME, EMAIL, "password", ENABLED);
        PasswordDTO passwordDTO = new PasswordDTO(ID, newPassword);
        when(mockUserRepository.findById(ID)).thenReturn(Optional.of(user));

        // when
        accountService.updatePassword(passwordDTO);

        // then
//        assertArrayEquals(newPassword, user.getPassword());
    }

    @Test
    void updatePassword_shouldThrowUserNotFoundException() {
        // given
        char[] newPassword = {'N', 'E', 'W'};
        PasswordDTO passwordDTO = new PasswordDTO(ID, newPassword);

        // when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> accountService.updatePassword(passwordDTO));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + ID + " nie istnieje."));
    }

    @Test
    @WithUserDetails("sky.guy@republic.co")
    void shouldDisplayCurrentUser() {
        // given
        User user = new User(FIRST_NAME, EMAIL, "password", ENABLED);
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(mockUserRepository.findByEmailAndIsDeleted(EMAIL, 0)).thenReturn(Optional.of(user));

        // when
        UserDTO userDTO = accountService.displayCurrentUser(authentication);

        // then
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getEnabled(), userDTO.getEnabled());
    }

    @Test
    @WithUserDetails("sky.guy@republic.co")
    void displayCurrentUser_shouldThrowUserNotFoundException() {
        // given
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(EMAIL);

        // when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> accountService.displayCurrentUser(authentication));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " nie istnieje."));
    }

    @Test
    void shouldUpdateUser() {
        // given
        User user = new User(FIRST_NAME, EMAIL, "password", ENABLED);
        UserDTO userDTO = new UserDTO(ID, FIRST_NAME, EMAIL, null, ENABLED);
        UserDetails userDetails = mock(UserDetails.class);
        when(mockUserRepository.findById(ID)).thenReturn(Optional.of(user));
        when(mockSpringDataUserDetailsService.loadUserByUsername(userDTO.getEmail())).thenReturn(userDetails);

        // when
        accountService.updateUser(userDTO);

        // then
        assertEquals(userDTO.getEmail(), user.getEmail());
        assertEquals(userDTO.getFirstName(), user.getFirstName());
    }

    @Test
    void updateUser_shouldThrowUserNotFoundException() {
        // given
        UserDTO userDTO = new UserDTO(ID, FIRST_NAME, EMAIL, null, ENABLED);

        // when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> accountService.updateUser(userDTO));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + ID + " nie istnieje."));
    }

    @Test
    void checkEmailExists_shouldThrowUserExistsException() {
        // given
        User user = new User(FIRST_NAME, EMAIL, "password", ENABLED);
        when(mockUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        UserExistsException thrown = assertThrows(UserExistsException.class, () -> accountService.checkEmailExists(EMAIL));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " już istnieje."));
    }
}