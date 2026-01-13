package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.StoreClient;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.payment.PaymentDTO;
import ru.yandex.practicum.dto.store.ProductDTO;
import ru.yandex.practicum.exception.NoPaymentFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentState;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String NOT_ENOUGH_INFORMATION_TO_CALCULATE_ORDER = "Недостаточно информации для расчёта заказа";

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StoreClient storeClient;
    private final OrderClient orderClient;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper, StoreClient storeClient, OrderClient orderClient) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.storeClient = storeClient;
        this.orderClient = orderClient;
    }

    @Override
    public PaymentDTO createPayment(OrderDTO orderDTO) {
        checkOrder(orderDTO);
        log.info("Создание новой сущности 'платеж'");

        Payment payment = Payment.builder()
                .orderId(orderDTO.getOrderId())
                .state(PaymentState.PENDING)
                .totalPayment(orderDTO.getTotalPrice())
                .deliveryTotal(orderDTO.getDeliveryPrice())
                .feeTotal(orderDTO.getTotalPrice() * vatAsNumber(10) + orderDTO.getTotalPrice())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        PaymentDTO paymentDTONew = paymentMapper.toPaymentDTO(savedPayment);
        log.info("Создание новой сущности 'платеж' с данными {}", paymentDTONew);

        return paymentDTONew;
    }

    private void checkOrder(OrderDTO orderDTO) {
        if (orderDTO.getDeliveryPrice() == null || orderDTO.getProductPrice() == null  ||
                orderDTO.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException(NOT_ENOUGH_INFORMATION_TO_CALCULATE_ORDER);
        }
    }

    @Override
    public Double calculationTotalCostOrder(OrderDTO orderDTO) {
        log.info("Расчет полной стоимости заказа с UUID - {}", orderDTO.getOrderId());

        double costGoods = calculatingCostGoodsInOrder(orderDTO.getProducts());
        double costGoodsWithVAT = costGoods * vatAsNumber(10) + costGoods;

        Double deliveryPrice = orderDTO.getDeliveryPrice();
        if (deliveryPrice == null) {
            throw new NotEnoughInfoInOrderToCalculateException(NOT_ENOUGH_INFORMATION_TO_CALCULATE_ORDER);
        }

        double result = costGoodsWithVAT + deliveryPrice;
        log.info("Полная стоимости заказа с UUID - {} вышла: {}", orderDTO.getOrderId(), result);

        return result;
    }

    private double calculatingCostGoodsInOrder(Map<UUID, Long> products) {
        log.info("Расчёт стоимости товаров в заказе");
        double allProductPrice = 0.0;

        for (Map.Entry<UUID, Long> entry : products.entrySet()) {
            ProductDTO product = storeClient.getProductById(entry.getKey());
            double productPrice = product.getPrice();

            allProductPrice += productPrice * entry.getValue();
        }
        log.info("Стоимости товаров в заказе составляет {}", allProductPrice);

        return allProductPrice;
    }

    private double vatAsNumber(double vatAsPercentage) {
        return vatAsPercentage / 100;
    }

    @Override
    @Transactional
    public void refund(UUID id) {
        Payment payment = changeState(id, PaymentState.SUCCESS);
        orderClient.orderPayment(payment.getOrderId());

        log.info("Платеж с UUID - {} успешно прошел", id);
    }

    private Payment changeState(UUID id, PaymentState paymentState) {
        Payment payment = getPaymentById(id);
        payment.setState(paymentState);

        return paymentRepository.save(payment);
    }

    private Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id).orElseThrow(() ->
                new NoPaymentFoundException(String.format("Платеж с UUID - %s не найден", id)));
    }

    @Override
    public Double productCost(OrderDTO orderDTO) {
        return calculatingCostGoodsInOrder(orderDTO.getProducts());
    }

    @Override
    @Transactional
    public void refusalOfPayment(UUID id) {
        Payment payment = changeState(id, PaymentState.FAILED);
        orderClient.orderPaymentFailed(payment.getOrderId());

        log.info("Платеж с UUID - {} отказан", id);
    }
}