package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequestDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.exception.CartNotFoundException;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.mapper.CartMapper;
import ru.yandex.practicum.model.Cart;
import ru.yandex.practicum.repository.CartRepository;
import ru.yandex.practicum.util.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final WarehouseClient warehouseClient;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper, WarehouseClient warehouseClient) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.warehouseClient = warehouseClient;
    }

    @Override
    public CartDTO getUserCurrentCart(String userName) {
        log.info("Получение корзины пользователя под именем {}", userName);

        CartDTO cartDTO = cartMapper.toCartDTO(getUserCart(userName));

        log.info("Корзина пользователя под именем {} успешно получена, корзина -> {}", userName, Json.simpleObjectToJson(cartDTO));

        return cartDTO;
    }

    private Cart getUserCart(String userName) {
        return cartRepository.findByUserName(userName).orElseThrow(
                () -> new CartNotFoundException(String.format("Корзина пользователя под именем - %s не найдена", userName)));
    }

    @Override
    @Transactional
    public CartDTO addProductToCart(String userName, Map<UUID, Long> products) {
        log.info("Добавление товаров в корзину пользователя под именем {}, товары -> {}", userName, Json.simpleObjectToJson(products));

        Cart cart = cartRepository.findByUserName(userName).orElse(createNewCart(userName));

        Map<UUID, Long> cartProducts = new HashMap<>();
        if (cart.getProducts() != null && !cart.getProducts().isEmpty()) {
            cartProducts.putAll(cart.getProducts());
        }

        cartProducts.putAll(products);
        cart.setProducts(cartProducts);

        CartDTO cartDTO = cartMapper.toCartDTO(cartRepository.save(cart));

        log.info("Товары в корзину успешно добавлены");

        return cartDTO;
    }

    private Cart createNewCart(String userName) {
        return new Cart(userName);
    }

    @Override
    @Transactional
    public void deactivateUserShoppingCart(String userName) {
        cartRepository.deleteByUserName(userName);

        log.info("Корзина пользователя под именем {} деактивирована", userName);
    }

    @Override
    @Transactional
    public CartDTO removeUserProduct(String userName, List<UUID> productsId) {
        log.info("Удаление продуктов из корзины пользователя под именем {}, продукту -> {}", userName, productsId);

        Cart cart = getUserCart(userName);

        log.info("Корзина -> {}", Json.simpleObjectToJson(cart));

        productsId.forEach(cart.getProducts()::remove);

        Cart savedCart = cartRepository.save(cart);
        CartDTO cartDTO = cartMapper.toCartDTO(savedCart);

        log.info("Продукты успешно удалены из корзины -> {}", cartDTO);

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO changeProductQuantity(String userName, ChangeProductQuantityRequestDTO changeProductQuantityRequestDTO) {
        log.info("Изменение количественного состояние продуктов в корзине пользователя под именем {}, UUID продукта {}, количество продукта {}",
                userName, changeProductQuantityRequestDTO.getProductId(), changeProductQuantityRequestDTO.getNewQuantity());

        Cart cart = getUserCart(userName);

        log.info("Корзина -> {}", Json.simpleObjectToJson(cart));

        if (!cart.getProducts().containsKey(changeProductQuantityRequestDTO.getProductId())) {
            throw new NoProductsInShoppingCartException(String.format("В корзине с UUID - %s нет такого товара.", cart.getId()));
        }

        cart.getProducts().put(changeProductQuantityRequestDTO.getProductId(), changeProductQuantityRequestDTO.getNewQuantity());

        Cart savedCart = cartRepository.save(cart);
        CartDTO cartDTO = cartMapper.toCartDTO(savedCart);

        log.info("Количество продуктов в корзине изменено, корзина -> {}", Json.simpleObjectToJson(cartDTO));

        return cartDTO;
    }

    @Override
    public BookedProductDTO checkAvailabilityProduct(String userName) {
        Cart cart = getUserCart(userName);

        return warehouseClient.checkAvailabilityProduct(cartMapper.toCartDTO(cart));
    }
}