package com.devwonder.product_service.mapper;

import com.devwonder.product_service.dto.ProductResponse;
import com.devwonder.product_service.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);
}