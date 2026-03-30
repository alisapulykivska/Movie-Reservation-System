package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CreateCustomUserDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnCustomUserDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateCustomUserDto;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserService {

    private final CustomUserRepository customUserRepository;

    public ReturnCustomUserDto createUser(CreateCustomUserDto user) {
        CustomUser customUser = new CustomUser();
        customUser.setUsername(user.username());
        customUser.setPassword(user.password());
        customUser.setEmail(user.email());
        CustomUser savedUser = customUserRepository.save(customUser);
        return new ReturnCustomUserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPassword(),
                savedUser.getEmail()
        );
    }

    public ReturnCustomUserDto updateUser(Long id, UpdateCustomUserDto user) {
        CustomUser oldUser = customUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(user == null) {
            throw new IllegalArgumentException("User information is null");
        } else {
            if(user.username() != null && !user.username().isEmpty()){
                oldUser.setUsername(user.username());
            }
            if(user.password() != null && !user.password().isEmpty()) {
                oldUser.setPassword(user.password());
            }
            if(user.email() != null && !user.email().isEmpty()) {
                oldUser.setEmail(user.email());
            }
        }
        CustomUser savedUser = customUserRepository.save(oldUser);
        return new ReturnCustomUserDto(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getPassword(),
                savedUser.getEmail()
        );
    }

    public void deleteUser(Long id) {
        customUserRepository.deleteById(id);
    }

    public ReturnCustomUserDto findUserById(Long id) {
        CustomUser user = customUserRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ReturnCustomUserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail()
        );
    }

    public List<ReturnCustomUserDto> findAllUsers() {
        return customUserRepository.findAll()
                .stream()
                .map(user -> new ReturnCustomUserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.getEmail()
                ))
                .toList();
    }
}
