package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.CreateSeatDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReturnSeatDto;
import com.alisa.moviereservationsystem.dto.updateDto.UpdateSeatDto;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.repositories.HallRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final HallRepository hallRepository;

    public ReturnSeatDto createSeat(CreateSeatDto seat) {
        Seat newSeat = new Seat();
        newSeat.setSeatNumber(seat.seatNumber());
        newSeat.setRowNumber(seat.rowNumber());
        newSeat.setPrice(seat.price());
        newSeat.setType(seat.type());
        newSeat.setStatus(seat.status());
        Hall hall = hallRepository.findById(seat.hallId())
                .orElseThrow(EntityNotFoundException::new);
        newSeat.setHall(hall);

        Seat savedSeat = seatRepository.save(newSeat);

        return new ReturnSeatDto(
                savedSeat.getId(),
                savedSeat.getSeatNumber(),
                savedSeat.getRowNumber(),
                savedSeat.getPrice(),
                savedSeat.getType(),
                savedSeat.getStatus(),
                savedSeat.getHall() != null ? savedSeat.getHall().getId() : null
        );
    }

    public ReturnSeatDto updateSeat(Long id, UpdateSeatDto seat) {
        Seat oldSeat = seatRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if(seat == null) {
            throw new IllegalArgumentException("Seat information is null");
        } else {
            if(seat.seatNumber() != null) {
                oldSeat.setSeatNumber(seat.seatNumber());
            }
            if(seat.rowNumber() != null) {
                oldSeat.setRowNumber(seat.rowNumber());
            }
            if(seat.price() != null) {
                oldSeat.setPrice(seat.price());
            }
        }
        Seat savedSeat = seatRepository.save(oldSeat);
        return new ReturnSeatDto(
                savedSeat.getId(),
                savedSeat.getSeatNumber(),
                savedSeat.getRowNumber(),
                savedSeat.getPrice(),
                savedSeat.getType(),
                savedSeat.getStatus(),
                savedSeat.getHall() != null ? savedSeat.getHall().getId() : null
        );
    }

    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    public ReturnSeatDto findSeatById(Long id) {
        Seat seat = seatRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return new ReturnSeatDto(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getRowNumber(),
                seat.getPrice(),
                seat.getType(),
                seat.getStatus(),
                seat.getHall() != null ? seat.getHall().getId() : null
        );
    }

    public List<ReturnSeatDto> findAllSeats() {
        return seatRepository.findAll()
                .stream()
                .map(seat -> new ReturnSeatDto(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getRowNumber(),
                        seat.getPrice(),
                        seat.getType(),
                        seat.getStatus(),
                        seat.getHall() != null ? seat.getHall().getId() : null
                )).toList();
    }

    public List<ReturnSeatDto> getAllSeatsForShowtime(Long showtimeId) {
        return seatRepository.findByShowtimes_Id(showtimeId)
                .stream().map(seat -> new ReturnSeatDto(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getRowNumber(),
                        seat.getPrice(),
                        seat.getType(),
                        seat.getStatus(),
                        seat.getHall() != null ? seat.getHall().getId() : null
                )).toList();
    }
}
