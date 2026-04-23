package com.alisa.moviereservationsystem.dto.paymentDto;

import java.math.BigDecimal;

public record PaymentRequestDto(Long reservationId, Long userId, BigDecimal generalPrice) {
}
