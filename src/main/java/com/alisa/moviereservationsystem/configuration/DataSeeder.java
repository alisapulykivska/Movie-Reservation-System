package com.alisa.moviereservationsystem.configuration;

import com.alisa.moviereservationsystem.models.CustomUser;
import com.alisa.moviereservationsystem.models.enums.UserRole;
import com.alisa.moviereservationsystem.repositories.CustomUserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final CustomUserRepository customUserRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        if (customUserRepository.findByUsername(adminUsername).isEmpty()) {
            CustomUser admin = new CustomUser();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setUserRole(UserRole.ADMIN);
            customUserRepository.save(admin);
        }
    }
}
