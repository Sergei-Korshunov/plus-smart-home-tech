package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.store.ProductAvailability;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDTO;

import java.util.UUID;

public interface StoreService {

    Page<ProductDTO> getProducts(ProductCategory productCategory, Pageable pageable);

    ProductDTO createNewProduct(ProductDTO productDTO);

    ProductDTO productUpdate(ProductDTO productDTO);

    boolean removeProductFromStore(UUID productId);

    boolean changeQuantityState(UUID productId, ProductAvailability quantityState);

    ProductDTO getProductById( UUID productId);
}