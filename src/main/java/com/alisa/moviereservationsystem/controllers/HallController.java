package com.alisa.moviereservationsystem.controllers;

import com.alisa.moviereservationsystem.dto.createDto.HallCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.HallReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.HallUpdateDto;
import com.alisa.moviereservationsystem.services.HallService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hall")
public class HallController {

    private final HallService hallService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HallReturnDto> createHall(@Valid @RequestBody HallCreateDto hall) {
        return ResponseEntity.ok(hallService.createHall(hall));
    }

    @PreAuthorize("hasRole('Admin')")
    @PutMapping("/{id}")
    public ResponseEntity<HallReturnDto> updateHall(@PathVariable Long id, @Valid @RequestBody HallUpdateDto hall) {
        return ResponseEntity.ok(hallService.updateHall(id, hall));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteHall(@PathVariable Long id) {
        hallService.deleteHall(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<HallReturnDto> findHallById(@PathVariable Long id) {
        return ResponseEntity.ok(hallService.findHallById(id));
    }

    @PreAuthorize("hasRole('ADIMN')")
    @GetMapping
    public ResponseEntity<List<HallReturnDto>> findAllHalls() {
        return ResponseEntity.ok(hallService.findAllHalls());
    }
}
