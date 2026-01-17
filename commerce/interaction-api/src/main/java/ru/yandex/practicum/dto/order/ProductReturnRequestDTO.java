package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ProductReturnRequestDTO {
    @NotNull
    private UUID orderId;

    @NotEmpty
    private Map<UUID, Long> products;
}