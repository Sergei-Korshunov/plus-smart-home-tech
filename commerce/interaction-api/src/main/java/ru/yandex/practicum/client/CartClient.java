package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.cart.CartDTO;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequestDTO;
import ru.yandex.practicum.dto.warehouse.BookedProductDTO;

import static ru.yandex.practicum.client.util.ValidationMessage.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "shopping-cart",
        path = "/api/v1/shopping-cart"
)
public interface CartClient {

    @GetMapping
    CartDTO getUserCurrentCart(@RequestParam(name = "username") @NotBlank(message = USER_NAME_CANNOT_BE_EMPTY) String userName);

    @PutMapping
    CartDTO addProductToCart(@RequestParam(name = "username") @NotBlank(message = USER_NAME_CANNOT_BE_EMPTY) String userName,
                             @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deactivateUserShoppingCart(@RequestParam(name = "username") @NotBlank(message = USER_NAME_CANNOT_BE_EMPTY) String userName);

    @PostMapping("/remove")
    CartDTO removeUserProduct(@RequestParam(name = "username") @NotBlank(message = USER_NAME_CANNOT_BE_EMPTY) String userName,
                              @RequestBody List<UUID> productsId);

    @PostMapping("/change-quantity")
    CartDTO changeProductQuantity(@RequestParam(name = "username") @NotBlank(message = USER_NAME_CANNOT_BE_EMPTY) String userName,
                                         @Valid @RequestBody ChangeProductQuantityRequestDTO changeProductQuantityRequestDTO);

    @PostMapping("/booking")
    BookedProductDTO checkAvailabilityProduct(@RequestParam(name = "username") @NotBlank(message = USER_NAME_CANNOT_BE_EMPTY) String userName);
}