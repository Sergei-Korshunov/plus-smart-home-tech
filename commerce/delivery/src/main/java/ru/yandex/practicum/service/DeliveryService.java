package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.delivery.DeliveryDTO;
import ru.yandex.practicum.dto.order.OrderDTO;

import java.util.UUID;

public interface DeliveryService {

    DeliveryDTO createNewDelivery(DeliveryDTO deliveryDTO);

    void successful(UUID deliveryId);

    void deliveryPicked(UUID deliveryId);

    void deliveryFailed(UUID deliveryId);

    Double deliveryCost(OrderDTO orderDTO);
}