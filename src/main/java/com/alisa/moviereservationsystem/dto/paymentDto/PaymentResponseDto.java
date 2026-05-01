package com.alisa.moviereservationsystem.dto.paymentDto;

import com.alisa.moviereservationsystem.models.enums.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponseDto(PaymentStatus status, Long reservationId, Long userId, BigDecimal generalPrice) {
}
