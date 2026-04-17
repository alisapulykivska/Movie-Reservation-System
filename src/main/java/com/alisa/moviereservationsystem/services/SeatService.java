package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.SeatCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.SeatReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.SeatUpdateDto;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.models.Showtime;
import com.alisa.moviereservationsystem.models.enums.SeatStatus;
import com.alisa.moviereservationsystem.repositories.HallRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import com.alisa.moviereservationsystem.repositories.ShowtimeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final HallRepository hallRepository;
    private final ShowtimeRepository showtimeRepository;

    public SeatReturnDto createSeat(SeatCreateDto seat) {
        Seat newSeat = new Seat();
        newSeat.setSeatNumber(seat.seatNumber());
        newSeat.setRowNumber(seat.rowNumber());
        newSeat.setPrice(seat.price());
        newSeat.setType(seat.type());
        newSeat.setStatus(SeatStatus.Available);
        Hall hall = hallRepository.findById(seat.hallId())
                .orElseThrow(() -> new InformationNotFoundException("Hall not found"));
        newSeat.setHall(hall);

        Seat savedSeat = seatRepository.save(newSeat);

        return toReturnDto(savedSeat);
    }

    public SeatReturnDto updateSeat(Long seatId, SeatUpdateDto seat) {
        Seat oldSeat = seatRepository.findById(seatId)
                .orElseThrow(() -> new InformationNotFoundException("Seat not found"));

                oldSeat.setSeatNumber(seat.seatNumber());
                oldSeat.setRowNumber(seat.rowNumber());
                oldSeat.setPrice(seat.price());

        Seat savedSeat = seatRepository.save(oldSeat);
        return toReturnDto(savedSeat);
    }

    public void deleteSeat(Long seatId) {
        seatRepository.deleteById(seatId);
    }

    public SeatReturnDto findSeatById(Long id) {
        Seat seat = seatRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Seat not found"));
        return toReturnDto(seat);
    }

    public List<SeatReturnDto> findAllSeats() {
        return seatRepository.findAll()
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    public List<SeatReturnDto> getAllSeatsForShowtime(Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new InformationNotFoundException("Showtime not found"));
        return showtime.getHall().getSeats()
                .stream()
                .map(this::toReturnDto)
                .toList();
    }

    private SeatReturnDto toReturnDto(Seat seat) {
        return new SeatReturnDto(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getRowNumber(),
                seat.getPrice(),
                seat.getType(),
                seat.getStatus(),
                seat.getHall().getId()
        );
    }

}
