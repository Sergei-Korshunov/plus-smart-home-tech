package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.dto.store.ProductAvailability;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductState;

import java.util.UUID;

@Entity
@Table(name = "products", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private String description;

    @Column(name = "image_src")
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private ProductAvailability productAvailability;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ProductState productState;

    private Double price;
}