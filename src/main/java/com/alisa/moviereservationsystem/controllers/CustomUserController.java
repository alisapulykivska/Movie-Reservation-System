package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CustomUserCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.CustomUserReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.CustomUserUpdateDto;
import com.alisa.moviereservationsystem.services.CustomUserService;
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
    public ResponseEntity<CustomUserReturnDto> createUser(@RequestBody CustomUserCreateDto user) {
        return ResponseEntity.ok(customUserService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomUserReturnDto> updateUser(@PathVariable Long id, @RequestBody CustomUserUpdateDto user) {
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
}
