package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseProductMapper {

    @Mapping(target = "width", source = "newProduct.dimension.width")
    @Mapping(target = "height", source = "newProduct.dimension.height")
    @Mapping(target = "depth", source = "newProduct.dimension.depth")
    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct toWarehouseProduct(NewProductInWarehouseRequest newProduct);
}