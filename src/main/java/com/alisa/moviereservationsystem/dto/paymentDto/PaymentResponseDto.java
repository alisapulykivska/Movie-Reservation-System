package com.alisa.moviereservationsystem.dto.paymentDto;

public record PaymentResponseDto(String status, Long reservationId, Long userId, Float generalPrice) {
}
