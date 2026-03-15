package com.alisa.moviereservationsystem.controllers;

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
    public ResponseEntity<CustomUser> createUser(@RequestBody CustomUser user){
        return ResponseEntity.ok(customUserService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomUser> updateUser(@PathVariable Long id, @RequestBody CustomUser user) {
        return ResponseEntity.ok(customUserService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        customUserService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomUser> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(customUserService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomUser>> findAllUsers() {
        return ResponseEntity.ok(customUserService.findAllUsers());
    }
}
