package ru.yandex.practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.order.CreateNewOrderRequestDTO;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.order.ProductReturnRequestDTO;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public List<OrderDTO> getOrders(String userName, Pageable pageable) {
        return orderService.getOrders(userName, pageable);
    }

    @Override
    public OrderDTO createNewOrder(CreateNewOrderRequestDTO createNewOrderRequestDTO) {
        return orderService.createNewOrder(createNewOrderRequestDTO);
    }

    @Override
    public OrderDTO productReturn(ProductReturnRequestDTO productReturnRequestDTO) {
        return orderService.productReturn(productReturnRequestDTO);
    }

    @Override
    public OrderDTO orderPayment(UUID orderId) {
        return orderService.orderPayment(orderId);
    }

    @Override
    public OrderDTO orderPaymentFailed(UUID orderId) {
        return orderService.orderPaymentFailed(orderId);
    }

    @Override
    public OrderDTO orderDelivery(UUID orderId) {
        return orderService.orderDelivery(orderId);
    }

    @Override
    public OrderDTO orderDeliveryFailed(UUID orderId) {
        return orderService.orderDeliveryFailed(orderId);
    }

    @Override
    public OrderDTO orderCompleted(UUID orderId) {
        return orderService.orderCompleted(orderId);
    }

    @Override
    public OrderDTO calculationOrderCost(UUID orderId) {
        return orderService.calculationOrderCost(orderId);
    }

    @Override
    public OrderDTO orderCalculateDelivery(UUID orderId) {
        return orderService.orderCalculateDelivery(orderId);
    }

    @Override
    public OrderDTO orderAssembly(UUID orderId) {
        return orderService.orderAssembly(orderId);
    }

    @Override
    public OrderDTO orderAssemblyFailed(UUID orderId) {
        return orderService.orderAssemblyFailed(orderId);
    }
}