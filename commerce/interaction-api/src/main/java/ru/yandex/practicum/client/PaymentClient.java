package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.payment.PaymentDTO;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping
    PaymentDTO createPayment(@RequestBody OrderDTO orderDTO);

    @PostMapping("/totalCost")
    Double calculationTotalCostOrder(@RequestBody OrderDTO orderDTO);

    @PostMapping("/refund")
    void refund(@RequestBody UUID id);

    @PostMapping("/productCost")
    Double productCost(@RequestBody OrderDTO orderDTO);

    @PostMapping("/failed")
    void refusalOfPayment(@RequestBody UUID id);
}