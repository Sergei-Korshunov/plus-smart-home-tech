package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "delivery", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "delivery_volume")
    private Double deliveryVolume;

    @Column(name = "delivery_weight")
    private Double deliveryWeight;

    private boolean fragile;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_address_id")
    private Address fromAddress;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_address_id")
    private Address toAddress;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private DeliveryState deliveryState;

    @Column(name = "order_id")
    private UUID orderId;
}