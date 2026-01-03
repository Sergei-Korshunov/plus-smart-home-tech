package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    private boolean fragile;

    @NotNull
    private DimensionDTO dimension;

    @NotNull
    @Min(value = 1)
    private Double weight;
}