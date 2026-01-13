package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.CartClient;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.client.PaymentClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.delivery.DeliveryDTO;
import ru.yandex.practicum.dto.order.CreateNewOrderRequestDTO;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequestDTO;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;
import ru.yandex.practicum.execption.NoOrderFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.util.Json;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartClient cartClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, CartClient cartClient,
                            WarehouseClient warehouseClient, PaymentClient paymentClient, DeliveryClient deliveryClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.cartClient = cartClient;
        this.warehouseClient = warehouseClient;
        this.paymentClient = paymentClient;
        this.deliveryClient = deliveryClient;
    }

    @Override
    public List<OrderDTO> getOrders(String userName, Pageable pageable) {
        log.info("Получение списка заказов");

        CartDTO cartDTO = cartClient.getUserCurrentCart(userName);

        PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Order> orders = orderRepository.findAllByCartId(cartDTO.getId(), page);

        List<OrderDTO> ordersDTONew = orders.getContent().stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
        log.info("Список заказов успешно получен {}", ordersDTONew);

        return ordersDTONew;
    }

    @Override
    @Transactional
    public OrderDTO createNewOrder(CreateNewOrderRequestDTO createNewOrderRequestDTO) {
        log.info("Создание новой сущности 'Заказ'");
        Order order = Order.builder()
                .products(
                        createNewOrderRequestDTO.getShoppingCart().getProducts().entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,e -> e.getValue().intValue())))
                .state(OrderState.NEW)
                .build();

        Order newOrder = orderRepository.save(order);

        BookedProductDTO bookedProducts = warehouseClient.assemblyProductForOrder(
                new AssemblyProductsForOrderRequest(
                        newOrder.getOrderId(),
                        createNewOrderRequestDTO.getShoppingCart().getProducts()
                ));

        newOrder.setFragile(bookedProducts.isFragile());
        newOrder.setDeliveryVolume(bookedProducts.getDeliveryVolume());
        newOrder.setDeliveryWeight(bookedProducts.getDeliveryWeight());
        newOrder.setProductPrice(paymentClient.productCost(orderMapper.toOrderDTO(newOrder)));

        DeliveryDTO deliveryDto = DeliveryDTO.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseClient.getAddress())
                .toAddress(createNewOrderRequestDTO.getDeliveryAddress())
                .build();
        newOrder.setDeliveryId(deliveryClient.createNewDelivery(deliveryDto).getDeliveryId());

        paymentClient.createPayment(orderMapper.toOrderDTO(newOrder));

        OrderDTO orderDTONew = orderMapper.toOrderDTO(newOrder);
        log.info("Заказ успешно создан с данными {}", Json.simpleObjectToJson(orderDTONew));

        return orderDTONew;
    }

    @Override
    @Transactional
    public OrderDTO productReturn(ProductReturnRequestDTO productReturnRequestDTO) {
        log.info("Запрос на возврат товара с данными {}", Json.simpleObjectToJson(productReturnRequestDTO));

        Order order = getOrderById(productReturnRequestDTO.getOrderId());
        warehouseClient.takeProductReturn(productReturnRequestDTO.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);

        OrderDTO orderDTONew = orderMapper.toOrderDTO(order);
        log.info("Запрос на возврат товара успешно выполнен с данными {}", Json.simpleObjectToJson(orderDTONew));

        return orderDTONew;
    }

    @Override
    @Transactional
    public OrderDTO orderPayment(UUID orderId) {
        return changeState(orderId, OrderState.PAID);
    }

    private OrderDTO changeState(UUID orderId, OrderState orderState) {
        Order order = getOrderById(orderId);
        order.setState(orderState);

        orderRepository.save(order);

        OrderDTO orderDTONew = orderMapper.toOrderDTO(order);
        log.info("Статус заказа изменен на {}", orderDTONew.getState());
        log.debug("Текущая информация о заказе - {}", Json.simpleObjectToJson(orderDTONew));

        return orderDTONew;
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() ->
                new NoOrderFoundException(String.format("Заказ с UUID - %s не найден", orderId)));
    }

    @Override
    @Transactional
    public OrderDTO orderPaymentFailed(UUID orderId) {
        return changeState(orderId, OrderState.PAYMENT_FAILED);
    }

    @Override
    @Transactional
    public OrderDTO orderDelivery(UUID orderId) {
        return changeState(orderId, OrderState.DELIVERED);
    }

    @Override
    @Transactional
    public OrderDTO orderDeliveryFailed(UUID orderId) {
        return changeState(orderId, OrderState.DELIVERY_FAILED);
    }

    @Override
    @Transactional
    public OrderDTO orderCompleted(UUID orderId) {
        return changeState(orderId, OrderState.COMPLETED);
    }

    @Override
    @Transactional
    public OrderDTO calculationOrderCost(UUID orderId) {
        log.info("Запрос на расчет полной стоимости заказа с UUID - {}", orderId);

        Order order = getOrderById(orderId);
        order.setTotalPrice(paymentClient.calculationTotalCostOrder(orderMapper.toOrderDTO(order)));

        orderRepository.save(order);

        OrderDTO orderDTONew = orderMapper.toOrderDTO(order);
        log.info("Запрос на расчет полной стоимости заказа с UUID - {} выполнен, данные {}",
                orderId, Json.simpleObjectToJson(orderDTONew));

        return orderDTONew;
    }

    @Override
    @Transactional
    public OrderDTO orderCalculateDelivery(UUID orderId) {
        log.info("Запрос на расчет полной стоимости доставки с UUID - {}", orderId);

        Order order = getOrderById(orderId);
        order.setDeliveryPrice(deliveryClient.deliveryCost(orderMapper.toOrderDTO(order)));

        orderRepository.save(order);

        OrderDTO orderDTONew = orderMapper.toOrderDTO(order);
        log.info("Запрос на расчет полной стоимости доставки с UUID - {} выполнен, данные {}",
                orderId, Json.simpleObjectToJson(orderDTONew));

        return orderDTONew;
    }

    @Override
    @Transactional
    public OrderDTO orderAssembly(UUID orderId) {
        return changeState(orderId, OrderState.ASSEMBLED);
    }

    @Override
    @Transactional
    public OrderDTO orderAssemblyFailed(UUID orderId) {
        return changeState(orderId, OrderState.ASSEMBLY_FAILED);
    }
}