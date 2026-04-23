package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.paymentDto.PaymentRequestDto;
import com.alisa.moviereservationsystem.dto.paymentDto.PaymentResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class PaymentService {

    private final RestClient restClient;

    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequest) {
        return restClient.post()
                .uri("http://localhost:8081/payment/process")
                .body(new PaymentRequestDto(
                        paymentRequest.reservationId(),
                        paymentRequest.userId(),
                        paymentRequest.generalPrice()))
                .retrieve()
                .body(PaymentResponseDto.class);
    }

    public PaymentResponseDto refundPayment(PaymentRequestDto paymentRequest) {
        return restClient.post()
                .uri("http://localhost:8081/payment/refund")
                .body(new PaymentRequestDto(
                        paymentRequest.reservationId(),
                        paymentRequest.userId(),
                        paymentRequest.generalPrice()))
                .retrieve()
                .body(PaymentResponseDto.class);
    }
}
