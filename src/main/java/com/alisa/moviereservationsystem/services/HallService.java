package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CreateHallDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnCustomUserDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnHallDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateHallDto;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.repositories.HallRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public ReturnHallDto createHall(CreateHallDto hall) {
        Hall newHall = new Hall();
        newHall.setHallNumber(hall.hallNumber());
        List<Seat> seats = seatRepository.findAllById(hall.seatIds());
        newHall.setSeats(seats);

        Hall savedHall = hallRepository.save(newHall);

        return new ReturnHallDto(
                savedHall.getId(),
                savedHall.getHallNumber(),
                savedHall.getSeats().stream().map(Seat :: getId).toList()
        );
    }

    public ReturnHallDto updateHall(Long id, UpdateHallDto hall) {
        Hall oldHall = hallRepository.findById(id)
                .orElseThrow(EntityExistsException::new);
        if(oldHall == null) {
            throw new IllegalArgumentException("Hall information is null");
        } else {
            if(hall.hallNumber() != null) {
                oldHall.setHallNumber(hall.hallNumber());
            }
        }
        Hall savedHall = hallRepository.save(oldHall);
        return new ReturnHallDto(
                savedHall.getId(),
                savedHall.getHallNumber(),
                savedHall.getSeats().stream().map(Seat :: getId).toList()
        );
    }

    public void deleteHall(Long id) {
        hallRepository.deleteById(id);
    }

    public ReturnHallDto findHallById(Long id) {
        Hall hall = hallRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ReturnHallDto(
                hall.getId(),
                hall.getHallNumber(),
                hall.getSeats().stream().map(Seat :: getId).toList()
        );
    }

    public List<ReturnHallDto> findAllHalls() {
        return hallRepository.findAll()
                .stream().
                map(hall -> new ReturnHallDto(
                      hall.getId(),
                      hall.getHallNumber(),
                      hall.getSeats().stream().map(Seat :: getId).toList()
                ))
                .toList();
    }
}
