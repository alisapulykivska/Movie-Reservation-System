package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.createDto.ReservationCreateDto;
import com.alisa.moviereservationsystem.dto.returnDto.ReservationReturnDto;
import com.alisa.moviereservationsystem.exceptions.InformationNotFoundException;
import com.alisa.moviereservationsystem.exceptions.PastShowtimeException;
import com.alisa.moviereservationsystem.exceptions.SeatsUnavailableException;
import com.alisa.moviereservationsystem.exceptions.WrongHallException;
import com.alisa.moviereservationsystem.models.*;
import com.alisa.moviereservationsystem.models.enums.*;
import com.alisa.moviereservationsystem.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceUnitTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private CustomUserRepository customUserRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private ShowtimeRepository showtimeRepository;
    @Mock private PaymentService paymentService;

    @InjectMocks
    private ReservationService reservationService;

    private Hall hall;
    private CustomUser user;
    private Seat seat;
    private Showtime showtime;

    @BeforeEach
    void setUp() {
        hall = new Hall();
        hall.setId(1L);
        hall.setHallNumber(1);

        user = new CustomUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setUserRole(UserRole.USER);

        seat = new Seat();
        seat.setId(10L);
        seat.setPrice(new BigDecimal("10.00"));
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setHall(hall);

        showtime = new Showtime();
        showtime.setId(5L);
        showtime.setDateTime(OffsetDateTime.now().plusDays(1));
        showtime.setShowtimeType(ShowtimeType.STANDARD);
        showtime.setStatus(ShowtimeStatus.UPCOMING);
        showtime.setHall(hall);
    }

    @Test
    void createReservation_ShouldReturnPendingReservation_WhenDataIsValid() {
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        Reservation saved = new Reservation();
        saved.setId(100L);
        saved.setUser(user);
        saved.setShowtime(showtime);
        saved.setSeats(List.of(seat));
        saved.setGeneralPrice(new BigDecimal("10.00"));
        saved.setStatus(ReservationStatus.PENDING);

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        ReservationReturnDto result = reservationService.createReservation(dto);

        assertNotNull(result);
        assertEquals(ReservationStatus.PENDING, result.status());
        assertEquals(user.getId(), result.userId());
        assertEquals(showtime.getId(), result.showtimeId());
        assertEquals(List.of(seat.getId()), result.seatIds());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void createReservation_ShouldApplyStandardMultiplier_WhenShowtimeIsStandard() {
        seat.setPrice(new BigDecimal("20.00"));

        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationReturnDto result = reservationService.createReservation(dto);

        // 20.00 * 1.0 = 20.00
        assertEquals(0, new BigDecimal("20.00").compareTo(result.generalPrice()));
    }

    @Test
    void createReservation_ShouldApplyPremiereMultiplier_WhenShowtimeIsPremiere() {
        seat.setPrice(new BigDecimal("20.00"));
        showtime.setShowtimeType(ShowtimeType.PREMIERE);

        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationReturnDto result = reservationService.createReservation(dto);

        // 20.00 * 1.5 = 30.00
        assertEquals(0, new BigDecimal("30.00").compareTo(result.generalPrice()));
    }

    @Test
    void createReservation_ShouldApplyPreviewMultiplier_WhenShowtimeIsPreview() {
        seat.setPrice(new BigDecimal("20.00"));
        showtime.setShowtimeType(ShowtimeType.PREVIEW);

        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationReturnDto result = reservationService.createReservation(dto);

        // 20.00 * 2.0 = 40.00
        assertEquals(0, new BigDecimal("40.00").compareTo(result.generalPrice()));
    }

    @Test
    void createReservation_ShouldMarkSeatAsHeld_WhenReservationCreated() {
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        Reservation saved = new Reservation();
        saved.setId(100L);
        saved.setUser(user);
        saved.setShowtime(showtime);
        saved.setSeats(List.of(seat));
        saved.setGeneralPrice(new BigDecimal("10.00"));
        saved.setStatus(ReservationStatus.PENDING);

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);

        reservationService.createReservation(dto);

        assertEquals(SeatStatus.HELD, seat.getStatus());
        verify(seatRepository, times(1)).saveAll(List.of(seat));
    }

    @Test
    void createReservation_ShouldThrowException_WhenShowtimeNotFound() {
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.empty());

        assertThrows(InformationNotFoundException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ShouldThrowException_WhenShowtimeIsCompleted() {
        showtime.setStatus(ShowtimeStatus.COMPLETED);
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));

        assertThrows(PastShowtimeException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ShouldThrowException_WhenShowtimeDateTimeIsInPast() {
        showtime.setDateTime(OffsetDateTime.now().minusDays(1));
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));

        assertThrows(PastShowtimeException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ShouldThrowException_WhenSeatIsHeld() {
        seat.setStatus(SeatStatus.HELD);
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));

        assertThrows(SeatsUnavailableException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ShouldThrowException_WhenSeatIsUnavailable() {
        seat.setStatus(SeatStatus.UNAVAILABLE);
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));

        assertThrows(SeatsUnavailableException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ShouldThrowException_WhenSeatBelongsToDifferentHall() {
        Hall otherHall = new Hall();
        otherHall.setId(99L);
        otherHall.setHallNumber(2);
        seat.setHall(otherHall);

        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));

        assertThrows(WrongHallException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void createReservation_ShouldThrowException_WhenUserNotFound() {
        ReservationCreateDto dto = new ReservationCreateDto(user.getId(), List.of(seat.getId()), showtime.getId());

        when(seatRepository.findSeatsByIdIn(List.of(seat.getId()))).thenReturn(List.of(seat));
        when(showtimeRepository.findById(showtime.getId())).thenReturn(Optional.of(showtime));
        when(customUserRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(InformationNotFoundException.class, () -> reservationService.createReservation(dto));
        verify(reservationRepository, never()).save(any());
    }
}
