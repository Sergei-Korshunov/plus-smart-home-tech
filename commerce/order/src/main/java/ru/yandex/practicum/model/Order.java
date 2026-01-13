package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.order.OrderState;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID orderId;

    @Enumerated(value = EnumType.STRING)
    private OrderState state;

    @ElementCollection
    @CollectionTable(name="order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "delivery_volume")
    private Double deliveryVolume;

    @Column(name = "delivery_weight")
    private Double deliveryWeight;

    private boolean fragile;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "product_price")
    private Double productPrice;

    @Column(name = "delivery_price")
    private Double deliveryPrice;
}