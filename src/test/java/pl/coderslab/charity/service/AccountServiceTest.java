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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private final Long ID = 1L;
    private final String FIRST_NAME = "Anakin";
    private final String EMAIL = "sky.guy@republic.co";
    private final String PASSWORD = "password";
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
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
        Role role = new Role("ROLE_ADMIN");
        when(mockRoleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));

        // when
        accountService.saveNewUser(user, "ADMIN");

        // then
        assertNotEquals(PASSWORD, user.getPassword());
        assertTrue(user.getRoles().contains(role));
        assertNotNull(user.getUuid());
    }

    @Test
    void saveNewUser_shouldThrowUserExistsException() {
        // given
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
        when(mockUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        UserExistsException thrown = assertThrows(UserExistsException.class, () -> accountService.saveNewUser(user, "ADMIN"));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " już ustnieje."));
    }

    @Test
    void createAccount() {
        // given
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
        Role role = new Role("ROLE_USER");
        when(mockRoleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // when
        String result = accountService.createAccount(user);

        // then
        assertEquals("Udało sie! Zajrzyj na swojego maila (" + user.getEmail() + "), tam znajdziesz link aktywacyjny do Twojego konta.", result);
    }

    @Test
    void createAccount_shouldThrowUserExistsException() {
        // given
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
        when(mockUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        UserExistsException thrown = assertThrows(UserExistsException.class, () -> accountService.createAccount(user));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " już ustnieje."));
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
        boolean result = accountService.comparePasswords(PASSWORD, "XXX");

        // then
        assertFalse(result);
    }

    @Test
    void shouldUpdatePassword() {
        // given
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
        PasswordDTO passwordDTO = new PasswordDTO(ID, "newPassword");
        when(mockUserRepository.findById(ID)).thenReturn(Optional.of(user));

        // when
        accountService.updatePassword(passwordDTO);

        // then
        assertNotEquals(PASSWORD, user.getPassword());
    }

    @Test
    void updatePassword_shouldThrowUserNotFoundException() {
        // given
        PasswordDTO passwordDTO = new PasswordDTO(ID, "newPassword");

        // when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class, () -> accountService.updatePassword(passwordDTO));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + ID + " nie istnieje."));
    }

    @Test
    @WithUserDetails("sky.guy@republic.co")
    void shouldDisplayCurrentUser() {
        // given
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
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
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
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
        User user = new User(FIRST_NAME, EMAIL, PASSWORD, ENABLED);
        when(mockUserRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        // when
        UserExistsException thrown = assertThrows(UserExistsException.class, () -> accountService.checkEmailExists(user));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik " + EMAIL + " już ustnieje."));
    }
}