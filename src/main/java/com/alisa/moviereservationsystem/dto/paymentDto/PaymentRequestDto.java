package com.alisa.moviereservationsystem.dto.paymentDto;

public record PaymentRequestDto(Long reservationId, Long userId, Float generalPrice) {
}
