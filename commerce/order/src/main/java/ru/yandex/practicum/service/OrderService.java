package ru.yandex.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.dto.order.CreateNewOrderRequestDTO;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.order.ProductReturnRequestDTO;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    List<OrderDTO> getOrders(String userName, Pageable pageable);

    OrderDTO createNewOrder(CreateNewOrderRequestDTO createNewOrderRequestDTO);

    OrderDTO productReturn(ProductReturnRequestDTO productReturnRequestDTO);

    OrderDTO orderPayment(UUID orderId);

    OrderDTO orderPaymentFailed(UUID orderId);

    OrderDTO orderDelivery(UUID orderId);

    OrderDTO orderDeliveryFailed(UUID orderId);

    OrderDTO orderCompleted(UUID orderId);

    OrderDTO calculationOrderCost(UUID orderId);

    OrderDTO orderCalculateDelivery(UUID orderId);

    OrderDTO orderAssembly(UUID orderId);

    OrderDTO orderAssemblyFailed(UUID orderId);
}