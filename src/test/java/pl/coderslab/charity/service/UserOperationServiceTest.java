package pl.coderslab.charity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.RoleNotFoundException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserOperationServiceTest {

    private final Long USER_ID = 1L;
    private final String FIRST_NAME = "Anakin";
    private final String EMAIL = "sky.guy@republic.co";
    private final String PASSWORD = "password";
    private final int ENABLED = 1;
    private final int DISABLED = 0;
    private final int NOT_DELETED = 0;
    private final int DELETED = 1;

    private final Set<Role> ROLES = new HashSet<>() {{
        add(new Role("ROLE_ADMIN"));
    }};
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @InjectMocks
    private UserOperationService userOperationService;

    @Test
    void shouldFindUserByRole() {
        // given
        User user = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, ENABLED, NOT_DELETED, null, ROLES);
        Role role = new Role("ROLE_ADMIN");
        when(mockRoleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
        when(mockUserRepository.findByRoles(role)).thenReturn(List.of(user));

        // when
        List<User> users = userOperationService.findUsersByRole("ROLE_ADMIN");

        // then
        assertEquals(1, users.size());
        assertEquals(users.get(0), user);
    }

    @Test
    void findUserByRole_shouldThrowRoleNotFoundException() {
        // given
        String role = "ROLE_ADMIN";

        // when
        RoleNotFoundException thrown = assertThrows(RoleNotFoundException.class,
                () -> userOperationService.findUsersByRole(role));
        // then
        assertTrue(thrown.getMessage().contains("Nie znaleziono roli."));
    }

    @Test
    void shouldFindUserByEmail() {
        // given
        User user = new User();
        when(mockUserRepository.findByEmailAndIsDeleted(EMAIL, 0)).thenReturn(Optional.of(user));

        // when
        User result = userOperationService.findUserByEmail(EMAIL);

        //then
        assertEquals(user, result);
    }

    @Test
    void findUserByEmail_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.findUserByEmail(EMAIL));

        // then
        assertTrue(thrown.getMessage().contains("Nie znaleziono użytkownika z emailem: " + EMAIL + "."));
    }

    @Test
    void shouldGetUserDTO() {
        // given
        User user = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, ENABLED, NOT_DELETED, null, ROLES);
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        UserDTO result = userOperationService.getUserDTO(USER_ID);

        //then
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getEnabled(), result.getEnabled());
    }

    @Test
    void getUserDTO_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.getUserDTO(USER_ID));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + USER_ID + " nie istnieje."));
    }

    @Test
    void shouldUpdateUserData() {
        // given
        User user = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, ENABLED, NOT_DELETED, null, ROLES);
        UserDTO userDTO = new UserDTO(USER_ID, "Jango Fett", "jango.fett@empire.ds", ROLES, ENABLED);
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        userOperationService.updateUserData(userDTO);

        //then
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getEnabled(), userDTO.getEnabled());
    }

    @Test
    void UpdateUserData_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.getUserDTO(USER_ID));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + USER_ID + " nie istnieje."));
    }

    @Test
    void blockUserToggle_DisableUser() {
        // given
        User userEnabled = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, ENABLED, NOT_DELETED, null, ROLES);
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEnabled));

        // when
        userOperationService.blockUserToggle(USER_ID);

        // then
        assertEquals(0, userEnabled.getEnabled());
    }

    @Test
    void blockUserToggle_EnableUser() {
        // given
        User userEnabled = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, DISABLED, NOT_DELETED, null, ROLES);
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(userEnabled));

        // when
        userOperationService.blockUserToggle(USER_ID);

        // then
        assertEquals(1, userEnabled.getEnabled());
    }
    @Test
    void blockUserToggle_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.getUserDTO(USER_ID));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + USER_ID + " nie istnieje."));
    }

    @Test
    @WithUserDetails("sky.guy@republic.co")
    void shouldDeleteUser() {
        // give
        User user = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, ENABLED, NOT_DELETED, null, ROLES);
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        userOperationService.deleteUser(USER_ID, authentication);

        // then
        assertEquals(DELETED, user.getIsDeleted());
    }
    @Test
    void deleteUser_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.getUserDTO(USER_ID));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + USER_ID + " nie istnieje."));
    }

    @Test
    void shouldRecoverUser() {
        // give
        User user = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, ENABLED, DELETED, null, ROLES);
        when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // when
        userOperationService.recoverUser(USER_ID);

        // then
        assertEquals(NOT_DELETED, user.getIsDeleted());
    }
    @Test
    void recoverUser_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.getUserDTO(USER_ID));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + USER_ID + " nie istnieje."));
    }

    @Test
    void shouldActivateUser() {
        // given
        UUID uuid = UUID.randomUUID();
        User user = new User(USER_ID, FIRST_NAME, EMAIL, PASSWORD, DISABLED, DELETED, uuid, ROLES);
        when(mockUserRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        // when
        userOperationService.activateUser(uuid);

        // then
        assertEquals(ENABLED, user.getEnabled());
    }
    @Test
    void activateUser_shouldThrowUserNotFoundException() {
        // given + when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> userOperationService.getUserDTO(USER_ID));

        // then
        assertTrue(thrown.getMessage().contains("Użytkownik z id = " + USER_ID + " nie istnieje."));
    }
}