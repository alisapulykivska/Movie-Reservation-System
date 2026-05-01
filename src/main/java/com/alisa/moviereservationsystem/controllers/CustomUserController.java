package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.securityDto.ChangePasswordDto;
import com.alisa.moviereservationsystem.dto.securityDto.LoginUserDto;
import com.alisa.moviereservationsystem.dto.securityDto.RegisterUserDto;
import com.alisa.moviereservationsystem.dto.createDto.CustomUserCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.CustomUserReturnDto;
import com.alisa.moviereservationsystem.dto.securityDto.ResetPasswordDto;
import com.alisa.moviereservationsystem.dto.updateDto.CustomUserUpdateDto;
import com.alisa.moviereservationsystem.services.CustomUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class CustomUserController {

    private final CustomUserService customUserService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CustomUserReturnDto> createUser(@Valid @RequestBody CustomUserCreateDto user) {
        return ResponseEntity.ok(customUserService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomUserReturnDto> updateUser(@PathVariable Long id, @Valid @RequestBody CustomUserUpdateDto user) {
        return ResponseEntity.ok(customUserService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        customUserService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomUserReturnDto> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(customUserService.findUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CustomUserReturnDto>> findAllUsers() {
        return ResponseEntity.ok(customUserService.findAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/promote")
    public ResponseEntity<CustomUserReturnDto> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(customUserService.promoteToAdmin(id));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomUserReturnDto> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return ResponseEntity.ok(customUserService.register(registerUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<String> userLogin(@Valid @RequestBody LoginUserDto loginUserDto) {
        return ResponseEntity.ok(customUserService.login(loginUserDto));
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<CustomUserReturnDto> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        return ResponseEntity.ok(customUserService.changePassword(id, changePasswordDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<CustomUserReturnDto> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        return ResponseEntity.ok(customUserService.resetPassword(id, resetPasswordDto));
    }
}
