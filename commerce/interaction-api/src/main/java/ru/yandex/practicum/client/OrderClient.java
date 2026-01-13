package ru.yandex.practicum.client;

import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.util.ValidationMessage;
import ru.yandex.practicum.dto.order.CreateNewOrderRequestDTO;
import ru.yandex.practicum.dto.order.OrderDTO;
import ru.yandex.practicum.dto.order.ProductReturnRequestDTO;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @GetMapping
    List<OrderDTO> getOrders(@RequestParam(name = "username")
                             @NotBlank(message = ValidationMessage.USER_NAME_CANNOT_BE_EMPTY)
                             String userName,
                             Pageable pageable);

    @PutMapping
    OrderDTO createNewOrder(@RequestBody CreateNewOrderRequestDTO createNewOrderRequestDTO);

    @PostMapping("/return")
    OrderDTO productReturn(@RequestBody ProductReturnRequestDTO productReturnRequestDTO);

    @PostMapping("/payment")
    OrderDTO orderPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDTO orderPaymentFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDTO orderDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDTO orderDeliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDTO orderCompleted(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDTO calculationOrderCost(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDTO orderCalculateDelivery(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDTO orderAssembly(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDTO orderAssemblyFailed(@RequestBody UUID orderId);
}