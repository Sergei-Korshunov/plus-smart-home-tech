package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.model.Cart;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toCartDTO(Cart cart);

}