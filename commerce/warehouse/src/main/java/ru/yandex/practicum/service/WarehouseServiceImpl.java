package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.BookingNotFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseProductMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.WarehouseProduct;
import ru.yandex.practicum.repository.BookingRepository;
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
    private static final String THERE_NOT_ENOUGH_ITEMS_FROM_BASKET =
            "Товар из корзины не находится в требуемом количестве на складе";
    private final WarehouseProductRepository warehouseProductRepository;
    private final BookingRepository bookingRepository;
    private final WarehouseProductMapper warehouseProductMapper;

    @Autowired
    public WarehouseServiceImpl(WarehouseProductRepository warehouseProductRepository, BookingRepository bookingRepository, WarehouseProductMapper warehouseProductMapper) {
        this.warehouseProductRepository = warehouseProductRepository;
        this.bookingRepository = bookingRepository;
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
    public void transferProductForDelivery(DeliveryRequest deliveryRequest) {
        Booking booking = getBookingByOrderId(deliveryRequest.getOrderId());
        booking.setDeliveryId(deliveryRequest.getDeliveryId());

        bookingRepository.save(booking);

        log.info("Передача заказ с UUID - {} в доставку", deliveryRequest.getOrderId());
    }

    private Booking getBookingByOrderId(UUID orderId) {
        return bookingRepository.findByOrderId(orderId).orElseThrow(
                () -> new BookingNotFoundException(String.format("Бронирование заказа с UUID - %s не найдено", orderId)));
    }

    @Override
    @Transactional
    public void takeProductReturn(Map<UUID, Long> products) {
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseProductRepository.findAllById(
                products.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            WarehouseProduct warehouseProduct = warehouseProducts.get(entry.getKey());
            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + entry.getValue());
        }

        warehouseProductRepository.saveAll(warehouseProducts.values());

        log.info("Возврат товаров на склад");
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
                throw new ProductInShoppingCartLowQuantityInWarehouseException(THERE_NOT_ENOUGH_ITEMS_FROM_BASKET);
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
    public BookedProductDTO assemblyProductForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        Map<UUID, Long> orderProducts = assemblyProductsForOrder.getProducts();
        Map<UUID, WarehouseProduct> products = warehouseProductRepository.findAllById(orderProducts.keySet())
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        double weight = 0;
        double volume = 0;
        boolean fragile = false;
        for (Map.Entry<UUID, Long> cartProduct : orderProducts.entrySet()) {
            WarehouseProduct product = products.get(cartProduct.getKey());
            long newQuantity = product.getQuantity() - cartProduct.getValue();
            if (newQuantity < 0) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(THERE_NOT_ENOUGH_ITEMS_FROM_BASKET);
            }

            product.setQuantity(newQuantity);
            weight += product.getWeight() * cartProduct.getValue();
            volume += product.getHeight() * product.getWeight() * product.getDepth() * cartProduct.getValue();
            fragile = fragile || product.getFragile();
        }

        createBooking(assemblyProductsForOrder);
        warehouseProductRepository.saveAll(products.values());

        return new BookedProductDTO(
                weight,
                volume,
                fragile
        );
    }

    private void createBooking(AssemblyProductsForOrderRequest assemblyProductsForOrder) {
        Booking booking = Booking.builder()
                .orderId(assemblyProductsForOrder.getOrderId())
                .products(assemblyProductsForOrder.getProducts())
                .build();

        bookingRepository.save(booking);
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
        log.info("Получение адреса");

        String address = AddressUtil.getAddress();
        AddressDTO addressDTO = new AddressDTO(address, address, address, address, address);

        log.info("Адрес получен {}", Json.simpleObjectToJson(addressDTO));

        return addressDTO;
    }
}