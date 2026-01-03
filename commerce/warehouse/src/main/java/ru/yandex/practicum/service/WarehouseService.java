package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {

    void addNewProduct(NewProductInWarehouseRequest product);

    BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO);

    void acceptNewQuantityProduct(AddProductToWarehouseRequest acceptNewQuantityProduct);

    AddressDTO getAddress();
}