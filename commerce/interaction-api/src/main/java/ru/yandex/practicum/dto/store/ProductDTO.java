package ru.yandex.practicum.dto.store;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ProductDTO {
    private UUID productId;

    @NotBlank(message = "Имя продукта не может быть пустым")
    private String productName;

    @NotBlank(message = "Описание продукта не может быть пустым")
    private String description;

    private String imageSrc;

    @NotNull(message = "Категория продукта должна быть указана")
    private ProductCategory productCategory;

    @JsonProperty(value = "quantityState")
    @NotNull(message = "Доступность продукта долно быть указано")
    private ProductAvailability productAvailability;

    @NotNull(message = "Состояние продукта долно быть указано")
    private ProductState productState;

    @Min(value = 1)
    private Double price;
}