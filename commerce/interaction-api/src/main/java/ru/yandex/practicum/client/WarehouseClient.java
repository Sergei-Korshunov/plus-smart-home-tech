package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "warehouse",
        path = "/api/v1/warehouse",
        fallbackFactory = WarehouseClientFallbackFactory.class
)
public interface WarehouseClient {

    @PutMapping
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest product);

    @PostMapping("/shipped")
    void transferProductForDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest);

    @PostMapping("/return")
    void takeProductReturn(Map<UUID, Long> products);

    @PostMapping("/check")
    BookedProductDTO checkAvailabilityProduct(@RequestBody CartDTO cartDTO);

    @PostMapping("/assembly")
    BookedProductDTO assemblyProductForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest assemblyProductsForOrder);

    @PostMapping("/add")
    void acceptNewQuantityProduct(@RequestBody @Valid AddProductToWarehouseRequest acceptProduct);

    @GetMapping("/address")
    AddressDTO getAddress();
}