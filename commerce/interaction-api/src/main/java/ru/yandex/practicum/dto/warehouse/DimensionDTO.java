package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DimensionDTO {

    @NotNull
    @Min(value = 1)
    private Double width;

    @NotNull
    @Min(value = 1)
    private Double height;

    @NotNull
    @Min(value = 1)
    private Double depth;
}