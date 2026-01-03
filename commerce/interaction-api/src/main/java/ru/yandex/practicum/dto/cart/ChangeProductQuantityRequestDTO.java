package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequestDTO {

    @NotNull
    private UUID productId;

    @NotNull
    private Long newQuantity;
}