package ru.yandex.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.dto.warehouse.AddressDTO;

import java.util.UUID;

@Getter
@Setter
@Builder
public class DeliveryDTO {

    private UUID deliveryId;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;

    @NotNull
    private AddressDTO fromAddress;

    @NotNull
    private AddressDTO toAddress;
}