package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.payment.PaymentDTO;

import java.util.UUID;

public interface PaymentService {

    PaymentDTO createPayment(OrderDTO orderDTO);

    Double calculationTotalCostOrder(OrderDTO orderDTO);

    void refund(UUID id);

    Double productCost(OrderDTO orderDTO);

    void refusalOfPayment(UUID id);
}