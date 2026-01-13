package ru.yandex.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.client.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDTO;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.warehouse.AddressDTO;
import ru.yandex.practicum.dto.warehouse.DeliveryRequest;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
public class DeliveryServiceImpl implements DeliveryService {
    private static final String ADDRESS1 = "ADDRESS_1";
    private static final String ADDRESS2 = "ADDRESS_2";

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Value("${delivery.base_cost}")
    private double baseCost;

    @Value("${delivery.coefficient_for_warehouse_address_1}")
    private double coefficientForWarehouseAddress1;

    @Value("${delivery.coefficient_for_warehouse_address_2}")
    private double coefficientForWarehouseAddress2;

    @Value("${delivery.coefficient_for_fragility}")
    private double coefficientForFragility;

    @Value("${delivery.coefficient_for_weight}")
    private double coefficientForWeight;

    @Value("${delivery.coefficient_for_volume}")
    private double coefficientForVolume;

    @Value("${delivery.delivery_street_coefficient_does_not_match_warehouse_address}")
    private double deliveryStreetCoefficientDoesNotMatchWarehouseAddress;

    @Autowired
    public DeliveryServiceImpl(DeliveryRepository deliveryRepository, DeliveryMapper deliveryMapper,
                               OrderClient orderClient, WarehouseClient warehouseClient) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.orderClient = orderClient;
        this.warehouseClient = warehouseClient;
    }

    @Override
    public DeliveryDTO createNewDelivery(DeliveryDTO deliveryDTO) {
        log.info("Создание новой сущности 'доставка'");

        Delivery delivery = deliveryMapper.toDelivery(deliveryDTO);
        delivery.setDeliveryState(DeliveryState.CREATED);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        DeliveryDTO deliveryDTONew = deliveryMapper.toDeliveryDTO(savedDelivery);
        log.info("Создание новой сущности 'доставка' с данными {}", deliveryDTONew);

        return deliveryDTONew;
    }

    @Override
    @Transactional
    public void successful(UUID deliveryId) {
        Delivery delivery = changeState(deliveryId, DeliveryState.CANCELLED);
        orderClient.orderDelivery(delivery.getOrderId());

        log.info("Доставка заказа с UUID - {} успешно завершена", delivery.getOrderId());
    }

    private Delivery changeState(UUID deliveryId, DeliveryState deliveryState) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryState(deliveryState);

        return deliveryRepository.save(delivery);
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(() ->
                new NoDeliveryFoundException(String.format("Заказ на доставку с UUID - %s не найден", deliveryId)));
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID deliveryId) {
        log.info("Доставка заказа с UUID - {} успешно оплачен", deliveryId);

        Delivery delivery = changeState(deliveryId, DeliveryState.IN_PROGRESS);

        log.info("Заказа с UUID - {} находится на сборке", delivery.getOrderId());

        orderClient.orderAssembly(delivery.getOrderId());

        DeliveryRequest deliveryRequest = new DeliveryRequest(delivery.getOrderId(), delivery.getDeliveryId());
        warehouseClient.transferProductForDelivery(deliveryRequest);

        log.info("Заказа с UUID - {} успешно собран", delivery.getOrderId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID deliveryId) {
        Delivery delivery = changeState(deliveryId, DeliveryState.FAILED);

        orderClient.orderDeliveryFailed(delivery.getOrderId());

        log.info("Доставка заказа с UUID - {} провалена", delivery.getOrderId());
    }

    @Override
    public Double deliveryCost(OrderDTO orderDTO) {
        UUID orderId = orderDTO.getOrderId();
        log.info("Начало расчета стоимости доставки для заказа {}", orderId);

        Delivery delivery = getDeliveryByOrderId(orderId);
        AddressDTO warehouseAddress = getWarehouseAddress(orderId);

        double base = calculateBaseCost(orderId, warehouseAddress);
        double fragileCost = calculateFragileCost(orderId, orderDTO, base);
        double weightCost = calculateWeightCost(orderId, orderDTO);
        double volumeCost = calculateVolumeCost(orderId, orderDTO);
        double streetCost = calculateStreetCost(orderId, delivery, warehouseAddress);

        double totalCost = base + fragileCost + weightCost + volumeCost + streetCost;

        log.info("Расчет стоимости доставки для заказа с UUID - {} окончен. Итоговая сумма: {}", orderId, totalCost);

        return totalCost;
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(() ->
                new NoDeliveryFoundException(String.format("Заказ на доставку по UUID заказа - %s не найден", orderId)));
    }

    private AddressDTO getWarehouseAddress(UUID orderId) {
        AddressDTO warehouseAddress = warehouseClient.getAddress();
        log.info("Адрес склада для заказа с UUID - {}: город {}, улица {}",
                orderId, warehouseAddress.getCity(), warehouseAddress.getStreet());

        return warehouseAddress;
    }

    private double calculateBaseCost(UUID orderId, AddressDTO warehouseAddress) {
        double addressCost = switch (warehouseAddress.getCity()) {
            case ADDRESS1 -> baseCost * coefficientForWarehouseAddress1;
            case ADDRESS2 -> baseCost * coefficientForWarehouseAddress2;
            default -> throw new IllegalStateException(
                    String.format("Неизвестный адрес доставки: %s", warehouseAddress.getCity()));
        };

        double result = baseCost + addressCost;
        log.debug("Базовая ставка доставки для заказа с UUID - {} составляет {}", orderId, result);

        return result;
    }

    private double calculateFragileCost(UUID orderId, OrderDTO orderDto, double base) {
        if (orderDto.isFragile()) {
            double fragileSurcharge = base * coefficientForFragility;
            log.debug("Надбавка за хрупкость увеличивает стоимость доставки для заказа с UUID - {} до {}",
                    orderId, base + fragileSurcharge);

            return fragileSurcharge;
        }
        return 0.0;
    }

    private double calculateWeightCost(UUID orderId, OrderDTO orderDto) {
        double cost = orderDto.getDeliveryWeight() * coefficientForWeight;
        log.debug("Стоимость за вес заказа с UUID - {}: {}", orderId, cost);

        return cost;
    }

    private double calculateVolumeCost(UUID orderId, OrderDTO orderDto) {
        double cost = orderDto.getDeliveryVolume() * coefficientForVolume;
        log.debug("Стоимость за объем заказа с UUID - {}: {}", orderId, cost);

        return cost;
    }

    private double calculateStreetCost(UUID orderId, Delivery delivery, AddressDTO warehouseAddress) {
        if (!warehouseAddress.getStreet().equals(delivery.getToAddress().getStreet())) {
            double surcharge = baseCost * deliveryStreetCoefficientDoesNotMatchWarehouseAddress;
            log.debug("Дополнительная плата за разные улицы для заказа с UUID - {}: {}", orderId, surcharge);

            return surcharge;
        }
        return 0.0;
    }
}