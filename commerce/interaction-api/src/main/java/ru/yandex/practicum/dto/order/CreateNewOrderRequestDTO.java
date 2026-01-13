package ru.yandex.practicum.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.AddressDTO;

@Getter
@Setter
public class CreateNewOrderRequestDTO {
    @NotNull
    private CartDTO shoppingCart;

    @NotNull
    private AddressDTO deliveryAddress;
}