package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.securityDto.ChangePasswordDto;
import com.alisa.moviereservationsystem.dto.securityDto.LoginUserDto;
import com.alisa.moviereservationsystem.dto.securityDto.RegisterUserDto;
import com.alisa.moviereservationsystem.dto.createDto.CustomUserCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.CustomUserReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.CustomUserUpdateDto;
import com.alisa.moviereservationsystem.exceptions.IncorrectPasswordException;
import com.alisa.moviereservationsystem.exceptions.InformationIsNullException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.exceptions.UnauthorizedUserException;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.enums.UserRole;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import lombok.AllArgsConstructor;
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

    public CustomUserReturnDto createUser(CustomUserCreateDto user) {
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
                .orElseThrow(() -> new InformationNotFoundException("CustomUser", userId));
        if(user == null) {
            throw new InformationIsNullException("User information is null");
        } else {
            if(user.username() != null && !user.username().isEmpty()){
                oldUser.setUsername(user.username());
            }
            if(user.email() != null && !user.email().isEmpty()) {
                oldUser.setEmail(user.email());
            }
        }
        CustomUser savedUser = customUserRepository.save(oldUser);
        return toReturnDto(savedUser);
    }

    public void deleteUser(Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId();
        if(!authenticatedUserId.equals(userId) &&
                customUserRepository.findById(userId).get().getUserRole() != UserRole.ADMIN){
            throw new UnauthorizedUserException("You can only delete your own information");
        }
        customUserRepository.deleteById(userId);
    }

    public CustomUserReturnDto findUserById(Long userId) {
        CustomUser user = customUserRepository.findById(userId).orElseThrow(() ->
                new InformationNotFoundException("CustomUser", userId));
        return toReturnDto(user);
    }

    public List<CustomUserReturnDto> findAllUsers() {
        return customUserRepository.findAll()
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    public CustomUserReturnDto promoteToAdmin(Long userId) {
        CustomUser customUser =  customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("CustomUser", userId));
        customUser.setUserRole(UserRole.ADMIN);
        return toReturnDto(customUserRepository.save(customUser));
    }

    public CustomUserReturnDto register(RegisterUserDto user) {
        CustomUser customUser = new CustomUser();
        customUser.setUsername(user.username());
        customUser.setEmail(user.email());
        customUser.setPassword(encoder.encode(user.password()));
        customUser.setUserRole(UserRole.USER);
        return toReturnDto(customUserRepository.save(customUser));
    }

    public String login(LoginUserDto user) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        user.username(),
                        user.password()));

        if(authentication.isAuthenticated()) {
            CustomUser customUser = customUserRepository.findByUsername(user.username())
                    .orElseThrow(() -> new InformationNotFoundException("User", 0L));
            return jwtService.generateToken(customUser.getUsername(), customUser.getId());
        }
        return "Login failed";
    }

    public CustomUserReturnDto changePassword(Long userId, ChangePasswordDto dto) {
        Long authenticatedUserId = getAuthenticatedUserId();
        if(!authenticatedUserId.equals(userId) &&
                customUserRepository.findById(userId).get().getUserRole() != UserRole.ADMIN){
            throw new UnauthorizedUserException("You can only change your own password");
        }
        CustomUser user = customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("CustomUser", userId));

        if(!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        return toReturnDto(customUserRepository.save(user));
    }

    public CustomUserReturnDto resetPassword(Long userId, String newPassword) {
        CustomUser user = customUserRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException("CustomUser", userId));
        user.setPassword(passwordEncoder.encode(newPassword));
        return toReturnDto(customUserRepository.save(user));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        CustomUser user = customUserRepository.findByUsername(username)
                .orElseThrow(() -> new InformationNotFoundException("User", 0L));
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
