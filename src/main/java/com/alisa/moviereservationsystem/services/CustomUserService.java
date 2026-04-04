package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CustomUserCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.CustomUserReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.CustomUserUpdateDto;
import com.alisa.moviereservationsystem.exceptions.InformationIsNullException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.enums.UserRole;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserService {

    private final CustomUserRepository customUserRepository;

    private CustomUserReturnDto toDto(CustomUser customUser) {
        return new CustomUserReturnDto(
                customUser.getId(),
                customUser.getUsername(),
                customUser.getPassword(),
                customUser.getEmail(),
                customUser.getUserRole()
        );
    }

    public CustomUserReturnDto createUser(CustomUserCreateDto user) {
        CustomUser customUser = new CustomUser();
        customUser.setUsername(user.username());
        customUser.setPassword(user.password());
        customUser.setEmail(user.email());
        CustomUser savedUser = customUserRepository.save(customUser);
        return toDto(savedUser);
    }

    public CustomUserReturnDto updateUser(Long id, CustomUserUpdateDto user) {
        CustomUser oldUser = customUserRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("CustomUser", id));
        if(user == null) {
            throw new InformationIsNullException("User information is null");
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
        return toDto(savedUser);
    }

    public void deleteUser(Long id) {
        customUserRepository.deleteById(id);
    }

    public CustomUserReturnDto findUserById(Long id) {
        CustomUser user = customUserRepository.findById(id).orElseThrow(() ->
                new InformationNotFoundException("CustomUser", id));
        return toDto(user);
    }

    public List<CustomUserReturnDto> findAllUsers() {
        return customUserRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CustomUserReturnDto promoteToAdmin(Long id) {
        CustomUser customUser =  customUserRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("CustomUser", id));
        customUser.setUserRole(UserRole.ADMIN);
        return toDto(customUserRepository.save(customUser));
    }
}
