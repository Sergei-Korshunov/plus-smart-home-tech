package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.dto.delivery.DeliveryDTO;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Override
    public DeliveryDTO createNewDelivery(DeliveryDTO deliveryDTO) {
        return deliveryService.createNewDelivery(deliveryDTO);
    }

    @Override
    public void successful(UUID deliveryId) {
        deliveryService.successful(deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) {
        deliveryService.deliveryPicked(deliveryId);
    }

    @Override
    public void deliveryFailed(UUID deliveryId) {
        deliveryService.deliveryFailed(deliveryId);
    }

    @Override
    public Double deliveryCost(OrderDTO orderDTO) {
        return deliveryService.deliveryCost(orderDTO);
    }
}