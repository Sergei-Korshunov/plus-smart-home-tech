package ru.yandex.practicum.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssemblyProductsForOrderRequest {
    private UUID orderId;
    private Map<UUID, Long> products;
}