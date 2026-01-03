package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.WarehouseProductRepository;
import ru.yandex.practicum.util.Json;
import ru.yandex.practicum.utils.AddressUtil;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseProductRepository warehouseProductRepository;
    private final WarehouseProductMapper warehouseProductMapper;

    public WarehouseServiceImpl(WarehouseProductRepository warehouseProductRepository, WarehouseProductMapper warehouseProductMapper) {
        this.warehouseProductRepository = warehouseProductRepository;
        this.warehouseProductMapper = warehouseProductMapper;
    }

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest product) {
        log.info("Добавление нового продукта на склад, продукт -> {}", Json.simpleObjectToJson(product));

        if (warehouseProductRepository.existsById(product.getProductId())) {
            log.error("Продукт с UUID - {} уже существует на складе", product.getProductId());
            throw new SpecifiedProductAlreadyInWarehouseException(
                    String.format("Товар под UUID - %s  уже зарегистрирован", product.getProductId()));
        }

        warehouseProductRepository.save(warehouseProductMapper.toWarehouseProduct(product));

        log.info("Продукт успешно добавлен на склад");
    }

    @Override
    @Transactional
    public BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO) {
        log.info("Проверка наличия товаров из корзины на складе, корзина -> {}", Json.simpleObjectToJson(cartDTO));

        Map<UUID, Long> cartProducts = cartDTO.getProducts();
        Map<UUID, WarehouseProduct> products = warehouseProductRepository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        if (products.size() != cartProducts.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouseException("Некоторых товаров нет на складе");
        }

        double weight = 0;
        double volume = 0;
        boolean fragile = false;
        for (Map.Entry<UUID, Long> cartProduct : cartProducts.entrySet()) {
            WarehouseProduct product = products.get(cartProduct.getKey());
            if (cartProduct.getValue() > product.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Товар из корзины не находится в требуемом количестве на складе");
            }
            weight += product.getWeight() * cartProduct.getValue();
            volume += product.getHeight() * product.getWeight() * product.getDepth() * cartProduct.getValue();
            fragile = fragile || product.getFragile();
        }

        BookedProductDTO bookedProductDTO = new BookedProductDTO(weight, volume, fragile);

        log.info("Товаров из корзины достаточно на складе, забронированные товары -> {}", Json.simpleObjectToJson(bookedProductDTO));

        return bookedProductDTO;
    }

    @Override
    @Transactional
    public void acceptNewQuantityProduct(AddProductToWarehouseRequest acceptNewQuantityProduct) {
        log.info("Поступление нового количества товара с UUID {}, количество {}",
                acceptNewQuantityProduct.getProductId(), acceptNewQuantityProduct.getQuantity());

        WarehouseProduct product = getWarehouseProduct(acceptNewQuantityProduct.getProductId());
        long newQuantity = product.getQuantity() + acceptNewQuantityProduct.getQuantity();
        product.setQuantity(newQuantity);
        warehouseProductRepository.save(product);

        log.info("Количество товара успешно обновлено, товар -> {}", Json.simpleObjectToJson(product));
    }

    private WarehouseProduct getWarehouseProduct(UUID productId) {
        return warehouseProductRepository.findById(productId).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе")
        );
    }

    @Override
    public AddressDTO getAddress() {
        log.info("Получение адресса");

        String address = AddressUtil.getAddress();
        AddressDTO addressDTO = new AddressDTO(address, address, address, address, address);

        log.info("Адресс получен {}", Json.simpleObjectToJson(addressDTO));

        return addressDTO;
    }
}