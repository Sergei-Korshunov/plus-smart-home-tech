package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.dto.store.ProductAvailability;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDTO;
import ru.yandex.practicum.service.StoreService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
public class StoreController implements StoreClient {
    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @Override
    public Page<ProductDTO> getProducts(@RequestParam String category, Pageable pageable) {
        return storeService.getProducts(ProductCategory.valueOf(category), pageable);
    }

    @Override
    public ProductDTO createNewProduct(ProductDTO productDTO) {
        return storeService.createNewProduct(productDTO);
    }

    @Override
    public ProductDTO productUpdate(@Valid @RequestBody ProductDTO productDTO) {
        return storeService.productUpdate(productDTO);
    }

    @Override
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return storeService.removeProductFromStore(productId);
    }

    @Override
    public boolean changeQuantityState(UUID productId, ProductAvailability quantityState) {
        return storeService.changeQuantityState(productId, quantityState);
    }

    @Override
    public ProductDTO getProductById(UUID productId) {
        return storeService.getProductById(productId);
    }
}