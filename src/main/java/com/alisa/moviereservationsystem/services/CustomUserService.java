package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserService {

    private final CustomUserRepository customUserRepository;

    public CustomUser createUser(CustomUser user) {
        return customUserRepository.save(user);
    }

    public CustomUser updateUser(Long id, CustomUser user) {
        CustomUser oldUser = customUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(user == null) {
            throw new IllegalArgumentException("User information is null");
        } else {
            if(user.getUsername() != null && !user.getUsername().isEmpty()){
                oldUser.setUsername(user.getUsername());
            }
            if(user.getPassword() != null && !user.getPassword().isEmpty()) {
                oldUser.setPassword(user.getPassword());
            }
            if(user.getEmail() != null && !user.getEmail().isEmpty()) {
                oldUser.setEmail(user.getEmail());
            }
        } return customUserRepository.save(oldUser);
    }

    public void deleteUser(Long id) {
        customUserRepository.deleteById(id);
    }

    public CustomUser findUserById(Long id) {
        return customUserRepository .findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<CustomUser> findAllUsers() {
        return customUserRepository.findAll();
    }
}
