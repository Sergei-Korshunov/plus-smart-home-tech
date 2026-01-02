package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.store.ProductDTO;
import ru.yandex.practicum.model.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toProduct(ProductDTO productDto);

    ProductDTO toProductDTO(Product product);

}