package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDTO;
import ru.yandex.practicum.dto.order.OrderDTO;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDTO createNewDelivery(@RequestBody DeliveryDTO deliveryDTO);

    @PostMapping("/successful")
    void successful(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void deliveryPicked(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void deliveryFailed(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    Double deliveryCost(@RequestBody OrderDTO orderDTO);
}