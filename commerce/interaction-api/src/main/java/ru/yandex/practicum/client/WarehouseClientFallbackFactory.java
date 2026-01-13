package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.ServiceUnavailableException;

import java.util.Map;
import java.util.UUID;

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
            public void transferProductForDelivery(DeliveryRequest deliveryRequest) {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }

            @Override
            public void takeProductReturn(Map<UUID, Long> products) {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }

            @Override
            public BookedProductDTO checkAvailabilityProduct(CartDTO cartDTO) {
                throw new ServiceUnavailableException("Сервис склада временно недоступен", cause);
            }

            @Override
            public BookedProductDTO assemblyProductForOrder(AssemblyProductsForOrderRequest assemblyProductsForOrder) {
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