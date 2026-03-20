package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.models.enums.SeatType;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeatService {

    private SeatRepository seatRepository;

    public Seat createSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public Seat updateSeat(Long id, Seat seat) {
        Seat oldSeat = seatRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(seat == null) {
            throw new IllegalArgumentException("Seat information is null");
        } else {
            if(seat.getSeatNumber() != null) {
                oldSeat.setSeatNumber(seat.getSeatNumber());
            }
            if(seat.getRowNumber() != null) {
                oldSeat.setRowNumber(seat.getRowNumber());
            }
            if(seat.getPrice() != null) {
                oldSeat.setPrice(seat.getPrice());
            }
            if(seat.getType() != null) {
                oldSeat.setType(seat.getType());
            }
        }
        return seatRepository.save(oldSeat);
    }

    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    public Seat findSeatById(Long id) {
        return seatRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Seat> findAllSeats() {
        return seatRepository.findAll();
    }
}
