package com.alisa.moviereservationsystem.services;

import com.alisa.moviereservationsystem.dto.paymentDto.PaymentRequestDto;
import com.alisa.moviereservationsystem.dto.paymentDto.PaymentResponseDto;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class PaymentService {

    private final RestClient restClient;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentResponseDto processPayment(PaymentRequestDto paymentRequest) {
        log.info("Processing payment request of {} for reservation {}",
                paymentRequest.generalPrice(), paymentRequest.reservationId());

        PaymentResponseDto response = restClient.post()
                .uri("http://localhost:8081/payment/process")
                .body(new PaymentRequestDto(
                        paymentRequest.reservationId(),
                        paymentRequest.userId(),
                        paymentRequest.generalPrice()))
                .retrieve()
                .body(PaymentResponseDto.class);

        log.info("Payment response: {} for reservation {}", response, paymentRequest.reservationId());
        return response;
    }

    public PaymentResponseDto refundPayment(PaymentRequestDto paymentRequest) {
        log.info("Sending refund request of {} for reservation {}",
                paymentRequest.generalPrice(), paymentRequest.reservationId());

        PaymentResponseDto response = restClient.post()
                .uri("http://localhost:8081/payment/refund")
                .body(new PaymentRequestDto(
                        paymentRequest.reservationId(),
                        paymentRequest.userId(),
                        paymentRequest.generalPrice()))
                .retrieve()
                .body(PaymentResponseDto.class);

        log.info("Refund response: {} for reservation {}", response, paymentRequest.reservationId());
        return response;
    }
}
