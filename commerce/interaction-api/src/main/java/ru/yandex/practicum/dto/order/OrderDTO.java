package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class OrderDTO {

    @NotNull
    private UUID orderId;

    private OrderState state;

    @NotEmpty
    private Map<UUID, Long> products;

    private UUID cartId;

    private UUID deliveryId;

    private UUID paymentId;

    private Double deliveryVolume;

    private Double deliveryWeight;

    private boolean fragile;

    private Double totalPrice;

    private Double productPrice;

    private Double deliveryPrice;
}