package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.repositories.HallRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HallService {

    private final HallRepository hallRepository;

    public Hall createHall(Hall hall) {
        return hallRepository.save(hall);
    }

    public Hall updateHall(Long id, Hall hall) {
        Hall oldHall = hallRepository.findById(id)
                .orElseThrow(EntityExistsException::new);
        if(oldHall == null) {
            throw new IllegalArgumentException("Hall information is null");
        } else {
            if(hall.getHallNumber() != null) {
                oldHall.setHallNumber(hall.getHallNumber());
            }
        }
        return hallRepository.save(oldHall);
    }

    public void deleteHall(Long id) {
        hallRepository.deleteById(id);
    }

    public Hall findHallById(Long id) {
        return hallRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Hall> findAllHalls() {
        return hallRepository.findAll();
    }
}
