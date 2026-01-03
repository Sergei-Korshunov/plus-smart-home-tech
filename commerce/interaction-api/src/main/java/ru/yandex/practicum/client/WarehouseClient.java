package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@FeignClient(
        name = "warehouse",
        path = "/api/v1/warehouse",
        fallbackFactory = WarehouseClientFallbackFactory.class
)
public interface WarehouseClient {

    @PutMapping
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest product);

    @PostMapping("/check")
    BookedProductDTO checkAvailabilityProduct(@RequestBody CartDTO cartDTO);

    @PostMapping("/add")
    void acceptNewQuantityProduct(@RequestBody @Valid AddProductToWarehouseRequest acceptProduct);

    @GetMapping("/address")
    AddressDTO getAddress();
}