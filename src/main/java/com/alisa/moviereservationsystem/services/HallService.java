package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.HallCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.HallReturnDto;
import com.alisa.moviereservationsystem.dto.updateDto.HallUpdateDto;
import com.alisa.moviereservationsystem.exceptions.InformationIsNullException;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.models.Hall;
import com.alisa.moviereservationsystem.models.Seat;
import com.alisa.moviereservationsystem.repositories.HallRepository;
import com.alisa.moviereservationsystem.repositories.SeatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HallService {

    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;

    public HallReturnDto createHall(HallCreateDto hall) {
        Hall newHall = new Hall();
        newHall.setHallNumber(hall.hallNumber());
        List<Seat> seats = seatRepository.findAllById(hall.seatIds());
        newHall.setSeats(seats);

        Hall savedHall = hallRepository.save(newHall);

        return toReturnDto(savedHall);
    }

    public HallReturnDto updateHall(Long hallId, HallUpdateDto hall) {
        Hall oldHall = hallRepository.findById(hallId)
                .orElseThrow(() -> new InformationNotFoundException("Hall", hallId));
        if(hall == null) {
            throw new InformationIsNullException("Hall information is null");
        } else {
            if(hall.hallNumber() != null) {
                oldHall.setHallNumber(hall.hallNumber());
            }
        }
        Hall savedHall = hallRepository.save(oldHall);
        return toReturnDto(savedHall);
    }

    public void deleteHall(Long hallId) {
        hallRepository.deleteById(hallId);
    }

    public HallReturnDto findHallById(Long hallId) {
        Hall hall = hallRepository.findById(hallId).orElseThrow(() ->
                new InformationNotFoundException("Hall", hallId));
        return toReturnDto(hall);
    }

    public List<HallReturnDto> findAllHalls() {
        return hallRepository.findAll()
                .stream().
                map(this::toReturnDto)
                .toList();
    }

    private HallReturnDto toReturnDto(Hall hall) {
        return new HallReturnDto(
                hall.getId(),
                hall.getHallNumber(),
                hall.getSeats().stream().map(Seat :: getId).toList()
        );
    }
}
