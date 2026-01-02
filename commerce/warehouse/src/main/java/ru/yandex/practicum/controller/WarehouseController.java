package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseClient {
    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @Override
    public void addNewProduct(NewProductInWarehouseRequest product) {
        warehouseService.addNewProduct(product);
    }

    @Override
    public BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO) {
        return warehouseService.checkAvailabilityProduct(cartDTO);
    }

    @Override
    public void acceptNewQuantityProduct(AddProductToWarehouseRequest acceptNewQuantityProduct) {
        warehouseService.acceptNewQuantityProduct(acceptNewQuantityProduct);
    }

    @Override
    public AddressDTO getAddress() {
        return warehouseService.getAddress();
    }
}