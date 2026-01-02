package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequestDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {

    CartDTO getUserCurrentCart(String userName);

    CartDTO addProductToCart(String userName, Map<UUID, Long> products);

    void deactivateUserShoppingCart(String userName);

    CartDTO removeUserProduct(String userName, List<UUID> productsId);

    CartDTO changeProductQuantity(String userName, ChangeProductQuantityRequestDTO changeProductQuantityRequestDTO);

    BookedProductDTO checkAvailabilityProduct(String userName);
}