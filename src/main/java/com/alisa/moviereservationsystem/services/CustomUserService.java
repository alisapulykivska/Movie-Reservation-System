package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.advice.GlobalExceptionHandler;
import com.alisa.moviereservationsystem.dto.securityDto.ChangePasswordDto;
import com.alisa.moviereservationsystem.dto.securityDto.LoginUserDto;
import com.alisa.moviereservationsystem.dto.securityDto.RegisterUserDto;
import com.alisa.moviereservationsystem.dto.createDto.CustomUserCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.CustomUserReturnDto;
import com.alisa.moviereservationsystem.dto.securityDto.ResetPasswordDto;
import com.alisa.moviereservationsystem.dto.updateDto.CustomUserUpdateDto;
import com.alisa.moviereservationsystem.exceptions.DuplicateInformationException;
import com.alisa.moviereservationsystem.exceptions.IncorrectPasswordException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.exceptions.UnauthorizedUserException;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.enums.UserRole;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserService {

    private final CustomUserRepository customUserRepository;

    private final JWTService jwtService;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(CustomUserService.class);

    public CustomUserReturnDto createUser(CustomUserCreateDto user) {
        if(customUserRepository.findByUsername(user.username()).isPresent()) {
            throw new DuplicateInformationException("Username is already in use");
        }

        if(customUserRepository.findByEmail(user.email()).isPresent()) {
            throw new DuplicateInformationException("Email is already in use");
        }

        CustomUser customUser = new CustomUser();
        customUser.setUsername(user.username());
        customUser.setPassword(passwordEncoder.encode(user.password()));
        customUser.setEmail(user.email());
        CustomUser savedUser = customUserRepository.save(customUser);
        return toReturnDto(savedUser);
    }

    public CustomUserReturnDto updateUser(Long userId, CustomUserUpdateDto user) {
        Long authenticatedUserId = getAuthenticatedUserId();
        if(!authenticatedUserId.equals(userId) &&
                customUserRepository.findById(authenticatedUserId).get().getUserRole() != UserRole.ADMIN){
            throw new UnauthorizedUserException("You can only update your own information");
        }

        CustomUser oldUser = customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found"));

                oldUser.setUsername(user.username());
                oldUser.setEmail(user.email());

        CustomUser savedUser = customUserRepository.save(oldUser);
        return toReturnDto(savedUser);
    }

    public void deleteUser(Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        if(!authenticatedUserId.equals(userId) &&
                customUserRepository.findById(authenticatedUserId).get().getUserRole() != UserRole.ADMIN){
            throw new UnauthorizedUserException("You can only delete your own information");
        }
        customUserRepository.deleteById(userId);
    }

    public CustomUserReturnDto findUserById(Long userId) {
        CustomUser user = customUserRepository.findById(userId).orElseThrow(() ->
                new InformationNotFoundException("User not found"));
        return toReturnDto(user);
    }

    public List<CustomUserReturnDto> findAllUsers() {
        return customUserRepository.findAll()
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    public CustomUserReturnDto promoteToAdmin(Long userId) {
        log.info("Promoting user to admin {}", userId);

        CustomUser customUser =  customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found"));
        customUser.setUserRole(UserRole.ADMIN);

        log.info("User {} promoted to admin", userId);

        return toReturnDto(customUserRepository.save(customUser));
    }

    public CustomUserReturnDto register(RegisterUserDto user) {
        log.info("Registering user {}", user.username());

        if(customUserRepository.findByUsername(user.username()).isPresent()) {
            throw new DuplicateInformationException("Username is already in use");
        }

        if(customUserRepository.findByEmail(user.email()).isPresent()) {
            throw new DuplicateInformationException("Email is already in use");
        }

        CustomUser customUser = new CustomUser();
        customUser.setUsername(user.username());
        customUser.setEmail(user.email());
        customUser.setPassword(encoder.encode(user.password()));
        customUser.setUserRole(UserRole.USER);

        log.info("User {} registered successfully", user.username());

        return toReturnDto(customUserRepository.save(customUser));
    }

    public String login(LoginUserDto user) {
        log.info("Login attempt for user {}", user.username());

        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        user.username(),
                        user.password()));

        if(authentication.isAuthenticated()) {
            CustomUser customUser = customUserRepository.findByUsername(user.username())
                    .orElseThrow(() -> new InformationNotFoundException("User not found"));

            log.info("User {} logged in successfully", user.username());

            return jwtService.generateToken(customUser.getUsername(), customUser.getId());
        }

        log.warn("Failed login attempt for user {}", user.username());

        return "Invalid username or password";
    }

    public CustomUserReturnDto changePassword(Long userId, ChangePasswordDto dto) {
        log.info("Changing password for user {}", userId);

        Long authenticatedUserId = getAuthenticatedUserId();
        if(!authenticatedUserId.equals(userId) &&
                customUserRepository.findById(userId).get().getUserRole() != UserRole.ADMIN){
            throw new UnauthorizedUserException("You can only change your own password");
        }
        CustomUser user = customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found"));

        if(!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Old password is incorrect");
        }

        if(passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("New password is the same as the old password");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));

        log.info("Password changed for user {}", userId);

        return toReturnDto(customUserRepository.save(user));
    }

    public CustomUserReturnDto resetPassword(Long userId, ResetPasswordDto resetPasswordDto) {
        log.info("Resetting password for user {}", userId);

        CustomUser user = customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("User not found"));
        if(!resetPasswordDto.newPassword().equals(resetPasswordDto.confirmPassword())) {
            throw new IncorrectPasswordException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.newPassword()));

        log.info("Successful password reset for user {}", userId);

        return toReturnDto(customUserRepository.save(user));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        CustomUser user = customUserRepository.findByUsername(username)
                .orElseThrow(() -> new InformationNotFoundException("User not found"));
        return user.getId();
    }

    private CustomUserReturnDto toReturnDto(CustomUser customUser) {
        return new CustomUserReturnDto(
                customUser.getId(),
                customUser.getUsername(),
                customUser.getEmail(),
                customUser.getUserRole()
        );
    }
}
