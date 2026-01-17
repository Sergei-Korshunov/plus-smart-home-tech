package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void addNewProduct(NewProductInWarehouseRequest product);

    void transferProductForDelivery(DeliveryRequest deliveryRequest);

    void takeProductReturn(Map<UUID, Long> products);

    BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO);

    BookedProductDTO assemblyProductForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder);

    void acceptNewQuantityProduct(AddProductToWarehouseRequest acceptNewQuantityProduct);

    AddressDTO getAddress();
}