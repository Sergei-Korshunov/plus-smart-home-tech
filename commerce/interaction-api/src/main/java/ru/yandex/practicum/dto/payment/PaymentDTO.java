package ru.yandex.practicum.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentDTO {
    private UUID paymentId;

    private Double totalPayment;

    private Double deliveryTotal;

    private Double feeTotal;
}