package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CustomUserCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.CustomUserReturnDto;
import com.alisa.moviereservationsystem.dto.securityDto.ChangePasswordDto;
import com.alisa.moviereservationsystem.dto.securityDto.LoginUserDto;
import com.alisa.moviereservationsystem.dto.securityDto.RegisterUserDto;
import com.alisa.moviereservationsystem.dto.updateDto.CustomUserUpdateDto;
import com.alisa.moviereservationsystem.exceptions.DuplicateInformationException;
import com.alisa.moviereservationsystem.exceptions.IncorrectPasswordException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.exceptions.UnauthorizedUserException;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.enums.UserRole;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserServiceUnitTest {

    @Mock
    private CustomUserRepository customUserRepository;
    @Mock
    private JWTService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BCryptPasswordEncoder encoder;
    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomUserService customUserService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContext;

    private CustomUser user;
    private CustomUser admin;

    @BeforeEach
    void setUp() {
        mockedSecurityContext = mockStatic(SecurityContextHolder.class);

        customUserService = new CustomUserService(
                customUserRepository,
                jwtService,
                authenticationManager,
                encoder,
                passwordEncoder
        );

        user = new CustomUser();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("email");
        user.setPassword("encodedOldPassword");
        user.setUserRole(UserRole.USER);

        admin = new CustomUser();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setUserRole(UserRole.ADMIN);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContext.close();
    }

    @Test
    void registerUser_ShouldReturnUser_WhenDataIsUnique() {
        RegisterUserDto registerUser = new RegisterUserDto(
                "developer", "dev@gmail.com", "password123"
        );

        CustomUser savedUser = new CustomUser();
        savedUser.setId(3L);
        savedUser.setUsername(registerUser.username());
        savedUser.setEmail(registerUser.email());
        savedUser.setPassword("encodedPassword");
        savedUser.setUserRole(UserRole.USER);

        when(customUserRepository.findByUsername(registerUser.username())).thenReturn(Optional.empty());
        when(customUserRepository.findByEmail(registerUser.email())).thenReturn(Optional.empty());

        when(encoder.encode(registerUser.password())).thenReturn(savedUser.getPassword());

        when(customUserRepository.save(any(CustomUser.class))).thenReturn(savedUser);

        CustomUserReturnDto result = customUserService.register(registerUser);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.id());
        assertEquals(savedUser.getUsername(), result.username());
        assertEquals(savedUser.getEmail(), result.email());
        assertEquals(savedUser.getUserRole(), result.userRole());

        verify(customUserRepository, times(1)).save(any(CustomUser.class));
        verify(encoder, times(1)).encode(registerUser.password());
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        RegisterUserDto registerUser = new RegisterUserDto(
                "exists", "email@gmail.com", "password123"
        );

        when(customUserRepository.findByUsername(registerUser.username())).thenReturn(Optional.of(new CustomUser()));

        assertThrows(DuplicateInformationException.class, () -> customUserService.register(registerUser));
        verify(customUserRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        RegisterUserDto registerUser = new RegisterUserDto(
                "username", "exists@gmail.com", "password123"
        );

        when(customUserRepository.findByUsername(registerUser.username())).thenReturn(Optional.empty());
        when(customUserRepository.findByEmail(registerUser.email())).thenReturn(Optional.of(new CustomUser()));

        assertThrows(DuplicateInformationException.class, () -> customUserService.register(registerUser));
        verify(customUserRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldReturnUser_WhenDataIsUnique() {
        CustomUserCreateDto createUser = new CustomUserCreateDto(
                "developer", "password123", "dev@gmail.com"
        );

        CustomUser savedUser = new CustomUser();
        savedUser.setId(3L);
        savedUser.setUsername(createUser.username());
        savedUser.setEmail(createUser.email());
        savedUser.setPassword("encodedPassword");
        savedUser.setUserRole(UserRole.USER);

        when(customUserRepository.findByUsername(createUser.username())).thenReturn(Optional.empty());
        when(customUserRepository.findByEmail(createUser.email())).thenReturn(Optional.empty());

        when(passwordEncoder.encode(createUser.password())).thenReturn(savedUser.getPassword());

        when(customUserRepository.save(any(CustomUser.class))).thenReturn(savedUser);

        CustomUserReturnDto result = customUserService.createUser(createUser);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.id());
        assertEquals(savedUser.getUsername(), result.username());
        assertEquals(savedUser.getEmail(), result.email());
        assertEquals(savedUser.getUserRole(), result.userRole());

        verify(customUserRepository, times(1)).save(any(CustomUser.class));
        verify(passwordEncoder, times(1)).encode(createUser.password());
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        CustomUserCreateDto createUser = new CustomUserCreateDto(
                "exists", "email@gmail.com", "password123"
        );

        when(customUserRepository.findByUsername(createUser.username())).thenReturn(Optional.of(new CustomUser()));

        assertThrows(DuplicateInformationException.class, () -> customUserService.createUser(createUser));
        verify(customUserRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        CustomUserCreateDto createUser = new CustomUserCreateDto(
                "username", "exists@gmail.com", "password123"
        );

        when(customUserRepository.findByUsername(createUser.username())).thenReturn(Optional.empty());
        when(customUserRepository.findByEmail(createUser.email())).thenReturn(Optional.of(new CustomUser()));

        assertThrows(DuplicateInformationException.class, () -> customUserService.createUser(createUser));
        verify(customUserRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldReturnUser_WhenUserUpdatesHimself() {
        Long userId = 5L;
        CustomUserUpdateDto updateUser = new CustomUserUpdateDto(
                "newUsername", "newEmail"
        );

        CustomUser oldUser = new CustomUser();
        oldUser.setId(userId);
        oldUser.setUsername("oldUsername");
        oldUser.setEmail("oldEmail");
        oldUser.setUserRole(UserRole.USER);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(oldUser.getUsername());

        when(customUserRepository.findByUsername(oldUser.getUsername())).thenReturn(Optional.of(oldUser));
        when(customUserRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(customUserRepository.save(any(CustomUser.class))).thenReturn(oldUser);

        CustomUserReturnDto result = customUserService.updateUser(userId, updateUser);

        assertEquals(updateUser.username(), result.username());
        verify(customUserRepository).save(any(CustomUser.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserTriesToUpdateSomeoneElse() {
        Long userId = 1L;
        Long anotherUserId = 2L;
        CustomUserUpdateDto updateUser = new CustomUserUpdateDto(
                "hacker", "hacker@gmail.com"
        );

        CustomUser user = new CustomUser();
        user.setId(userId);
        user.setUsername("hacker_user");
        user.setUserRole(UserRole.USER);

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getUsername());

        when(customUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(customUserRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedUserException.class, () -> customUserService.updateUser(anotherUserId, updateUser));
        verify(customUserRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenAdminDeletesAnotherUser() {
        Long anotherUserId = 2L;

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(admin.getUsername());

        when(customUserRepository.findByUsername(admin.getUsername())).thenReturn(Optional.of(admin));
        when(customUserRepository.findById(admin.getId())).thenReturn(Optional.of(admin));

        customUserService.deleteUser(anotherUserId);
        verify(customUserRepository).deleteById(anotherUserId);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserTriesToDeleteSomeoneElse() {
        Long anotherUserId = 2L;

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getUsername());

        when(customUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedUserException.class, () -> customUserService.deleteUser(anotherUserId));
        verify(customUserRepository, never()).deleteById(anyLong());
    }

    @Test
    void findUserById_ShouldReturnUser_WhenFound() {
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));

        CustomUserReturnDto result = customUserService.findUserById(user.getId());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
    }

    @Test
    void findUserById_ShouldThrowException_WhenUserNotFound() {
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(InformationNotFoundException.class, () -> customUserService.findUserById(user.getId()));
    }

    @Test
    void promoteToAdmin_ShouldSetAdminRole_WhenFound() {
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(customUserRepository.save(any())).thenReturn(user);

        customUserService.promoteToAdmin(user.getId());

        assertEquals(UserRole.ADMIN, user.getUserRole());
        verify(customUserRepository).save(user);
    }

    @Test
    void promoteToAdmin_ShouldThrowException_WhenUserNotFound() {
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(InformationNotFoundException.class, () -> customUserService.promoteToAdmin(user.getId()));
    }

    @Test
    void changePassword_ShouldChangePassword_WhenValidData() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("wrongOldPassword", "newPassword");

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getUsername());

        when(customUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.matches(changePasswordDto.newPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(changePasswordDto.newPassword())).thenReturn(user.getPassword());
        when(customUserRepository.save(any(CustomUser.class))).thenReturn(user);

        CustomUserReturnDto result = customUserService.changePassword(user.getId(), changePasswordDto);
        assertNotNull(result);
        verify(customUserRepository).save(any());
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("wrongOld", "newPassword");

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getUsername());

        when(customUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(IncorrectPasswordException.class,
                () -> customUserService.changePassword(user.getId(), changePasswordDto));

        verify(customUserRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordSameAsOld() {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto("oldPassword", "oldPassword");

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        mockedSecurityContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(user.getUsername());

        when(customUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordDto.oldPassword(), user.getPassword())).thenReturn(true);

        assertThrows(IncorrectPasswordException.class,
                () -> customUserService.changePassword(user.getId(), changePasswordDto));

        verify(customUserRepository, never()).save(any());
    }

    @Test
    void loginUser_ShouldReturnToken_WhenDataIsValid() {
        LoginUserDto loginDto = new LoginUserDto("user", "user@gmail.com", "password123");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(customUserRepository.findByUsername(loginDto.username())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(loginDto.username(), user.getId())).thenReturn("mocked-jwt-token");

        String result = customUserService.login(loginDto);

        assertEquals("mocked-jwt-token", result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user.getUsername(), user.getId());
    }

    @Test
    void loginUser_ShouldThrowException_WhenPasswordIsNotValid() {
        LoginUserDto loginDto = new LoginUserDto("user", "user@gamil.com", "wrongPassword");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        String result = customUserService.login(loginDto);
        assertEquals("Invalid username or password", result);
        verify(customUserRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    void findAllUsers_ShouldReturnListOfUsers_WhenUsersExist() {
        CustomUser user1 = new CustomUser();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setPassword("password1");

        CustomUser user2 = new CustomUser();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setPassword("password2");

        when(customUserRepository.findAll()).thenReturn(List.of(user1, user2));

        List<CustomUserReturnDto> result = customUserService.findAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(user1.getUsername(), result.get(0).username());
        assertEquals(user2.getUsername(), result.get(1).username());

        verify(customUserRepository, times(1)).findAll();
    }
}