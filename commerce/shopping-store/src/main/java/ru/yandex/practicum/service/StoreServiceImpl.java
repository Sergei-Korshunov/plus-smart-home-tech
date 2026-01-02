package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.*;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.util.Json;

import java.util.UUID;

@Slf4j
@Service
public class StoreServiceImpl implements StoreService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    public StoreServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductDTO> getProducts(ProductCategory productCategory, Pageable pageable) {
        log.info("Поиск продукта по категории - {}", productCategory);

        Sort sort = pageable.getSort();

        if (sort.isEmpty()) {
            sort = Sort.by("productName");
        }

        PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<Product> products = productRepository.findAllByProductCategory(productCategory, page);
        Page<ProductDTO> productsDTO = products.map(productMapper::toProductDTO);

        log.info("По категории '{}' найдены следующие продукты - {}", productCategory, productsDTO);

        return productsDTO;
    }

    @Override
    public ProductDTO createNewProduct(ProductDTO productDTO) {
        log.info("Создание нового продукта на основе данных - {}", Json.simpleObjectToJson(productDTO));

        Product product = productMapper.toProduct(productDTO);
        Product saveProduct = productRepository.save(product);
        ProductDTO newProductDTO = productMapper.toProductDTO(saveProduct);

        log.info("Новый продукт успешно создан - {}", Json.simpleObjectToJson(newProductDTO));

        return newProductDTO;
    }

    @Override
    @Transactional
    public ProductDTO productUpdate(ProductDTO productDTO) {
        log.info("Обновление продукта на основе данных - {}", Json.simpleObjectToJson(productDTO));

        UUID productId = productDTO.getProductId();
        if (productId == null) {
            throw new IllegalArgumentException(String.format("Необходимо указать идентификатор товара для обновления"));
        }

        Product exitingProduct = getProduct(productDTO.getProductId());

        exitingProduct.setProductName(productDTO.getProductName());
        exitingProduct.setDescription(productDTO.getDescription());
        exitingProduct.setImageSrc(productDTO.getImageSrc());
        exitingProduct.setProductCategory(productDTO.getProductCategory());
        exitingProduct.setProductAvailability(productDTO.getProductAvailability());
        exitingProduct.setProductState(productDTO.getProductState());
        exitingProduct.setPrice(productDTO.getPrice());

        Product productUpdate = productRepository.save(exitingProduct);
        ProductDTO productUpdateDTO = productMapper.toProductDTO(productUpdate);

        log.info("Продукт успешно обновлен - {}", Json.simpleObjectToJson(productUpdateDTO));

        return productUpdateDTO;
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        log.info("Удаление с продажи продукта с UUID - {}", productId);

        Product product = getProduct(productId);
        product.setProductState(ProductState.DEACTIVATE);

        productRepository.save(product);

        log.info("Продукт с UUID - {} успешно удален с продажи", productId);

        return true;
    }

    @Override
    @Transactional
    public boolean changeQuantityState(UUID productId, ProductAvailability quantityState) {
        log.info("Изменение количественного состояния продукта по UUID - {} на колличество -> {}",
                productId, quantityState);

        Product product = getProduct(productId);
        product.setProductAvailability(quantityState);

        productRepository.save(product);

        log.info("Изменение количественного состояния продукта по UUID - {} успешно выполненно", productId);

        return true;
    }

    @Override
    public ProductDTO getProductById(UUID productId) {
        log.info("Получение продукта по UUID - {}", productId);

        ProductDTO productDTO = productMapper.toProductDTO(getProduct(productId));

        log.info("Продукт получен -> {}", Json.simpleObjectToJson(productDTO));

        return productDTO;
    }

    private Product getProduct(UUID productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException(String.format("Продукт с UUID - %s не найден", productId))
        );
    }
}