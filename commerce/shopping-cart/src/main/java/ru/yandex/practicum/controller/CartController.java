package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequestDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.service.CartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
public class CartController implements CartClient {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public CartDTO getUserCurrentCart(String userName) {
        return cartService.getUserCurrentCart(userName);
    }

    @Override
    public CartDTO addProductToCart(String userName, Map<UUID, Long> products) {
        return cartService.addProductToCart(userName, products);
    }

    @Override
    public void deactivateUserShoppingCart(String userName) {
        cartService.deactivateUserShoppingCart(userName);
    }

    @Override
    public CartDTO removeUserProduct(String userName, List<UUID> productsId) {
        return cartService.removeUserProduct(userName, productsId);
    }

    @Override
    public CartDTO changeProductQuantity(String userName, ChangeProductQuantityRequestDTO changeProductQuantityRequestDTO) {
        return cartService.changeProductQuantity(userName, changeProductQuantityRequestDTO);
    }

    @Override
    public BookedProductDTO checkAvailabilityProduct(String userName) {
        return cartService.checkAvailabilityProduct(userName);
    }
}