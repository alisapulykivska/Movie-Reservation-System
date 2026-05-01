package com.alisa.moviereservationsystem.repositories;

import com.alisa.moviereservationsystem.models.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomUserRepository extends JpaRepository<CustomUser, Long> {
    Optional<CustomUser> findByUsername(String username);

    Optional<CustomUser> findByEmail(String email);
}
