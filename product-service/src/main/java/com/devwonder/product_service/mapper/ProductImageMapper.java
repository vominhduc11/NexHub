package com.devwonder.product_service.mapper;

import com.devwonder.product_service.dto.ProductImageResponse;
import com.devwonder.product_service.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    
    @Mapping(target = "imageUrl", source = "url")
    @Mapping(target = "altText", source = "alt")
    @Mapping(target = "displayOrder", source = "orderPosition")
    @Mapping(target = "isPrimary", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProductImageResponse toResponse(ProductImage image);
}