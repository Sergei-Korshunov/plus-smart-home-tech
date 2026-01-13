package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.delivery.DeliveryDTO;
import ru.yandex.practicum.model.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    Delivery toDelivery(DeliveryDTO deliveryDTO);

    DeliveryDTO toDeliveryDTO(Delivery delivery);
}