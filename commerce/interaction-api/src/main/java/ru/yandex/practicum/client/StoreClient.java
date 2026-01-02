package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.ProductAvailability;
import ru.yandex.practicum.dto.store.ProductDTO;

import java.util.UUID;

@FeignClient(
        name = "shopping-store",
        path = "/api/v1/shopping-store"
)
public interface StoreClient {

    @GetMapping
    Page<ProductDTO> getProducts(@RequestParam(name = "category") String category, Pageable pageable);

    @PutMapping
    ProductDTO createNewProduct(@Valid @RequestBody ProductDTO productDTO);

    @PostMapping
    ProductDTO productUpdate(@Valid @RequestBody ProductDTO productDTO);

    @PostMapping("/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean changeQuantityState(@RequestParam UUID productId,
                                @RequestParam ProductAvailability quantityState);

    @GetMapping("/{productId}")
    ProductDTO getProductById(@PathVariable UUID productId);
}