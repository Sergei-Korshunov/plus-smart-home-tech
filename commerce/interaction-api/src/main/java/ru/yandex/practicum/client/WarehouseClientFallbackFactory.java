package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.ServiceUnavailableException;

@Component
public class WarehouseClientFallbackFactory implements FallbackFactory<WarehouseClient> {

    @Override
    public WarehouseClient create(Throwable cause) {
        return new WarehouseClient() {
            @Override
            public void addNewProduct(NewProductInWarehouseRequest product) {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }

            @Override
            public BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO) {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }

            @Override
            public void acceptNewQuantityProduct(AddProductToWarehouseRequest acceptProduct) {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }

            @Override
            public AddressDTO getAddress() {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }
        };
    }
}