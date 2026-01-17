package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

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
    public void transferProductForDelivery(DeliveryRequest deliveryRequest) {
        warehouseService.transferProductForDelivery(deliveryRequest);
    }

    @Override
    public void takeProductReturn(Map<UUID, Long> products) {
        warehouseService.takeProductReturn(products);
    }

    @Override
    public BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO) {
        return warehouseService.checkAvailabilityProduct(cartDTO);
    }

    @Override
    public BookedProductDTO assemblyProductForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        return warehouseService.assemblyProductForOrder(assemblyProductsForOrder);
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