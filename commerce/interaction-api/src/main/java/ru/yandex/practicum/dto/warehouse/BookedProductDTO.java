package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookedProductDTO {

    @NotBlank
    private Double deliveryWeight;

    @NotBlank
    private Double deliveryVolume;

    private boolean fragile;
}