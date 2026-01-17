package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.payment.PaymentDTO;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public PaymentDTO createPayment(OrderDTO orderDTO) {
        return paymentService.createPayment(orderDTO);
    }

    @Override
    public Double calculationTotalCostOrder(OrderDTO orderDTO) {
        return paymentService.calculationTotalCostOrder(orderDTO);
    }

    @Override
    public void refund(UUID id) {
        paymentService.refund(id);
    }

    @Override
    public Double productCost(OrderDTO orderDTO) {
        return paymentService.productCost(orderDTO);
    }

    @Override
    public void refusalOfPayment(UUID id) {
        paymentService.refusalOfPayment(id);
    }
}