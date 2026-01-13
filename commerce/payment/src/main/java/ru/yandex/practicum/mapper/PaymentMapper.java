package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.payment.PaymentDTO;
import ru.yandex.practicum.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentDTO toPaymentDTO(Payment payment);
}