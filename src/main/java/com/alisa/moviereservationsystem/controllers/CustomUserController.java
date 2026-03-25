package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.CreateCustomUserDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnCustomUserDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateCustomUserDto;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.services.CustomUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class CustomUserController {

    private final CustomUserService customUserService;

    @PostMapping
    public ResponseEntity<ReturnCustomUserDto> createUser(@RequestBody CreateCustomUserDto user) {
        return ResponseEntity.ok(customUserService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReturnCustomUserDto> updateUser(@PathVariable Long id, @RequestBody UpdateCustomUserDto user) {
        return ResponseEntity.ok(customUserService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        customUserService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnCustomUserDto> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(customUserService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReturnCustomUserDto>> findAllUsers() {
        return ResponseEntity.ok(customUserService.findAllUsers());
    }
}
