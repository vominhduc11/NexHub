package com.devwonder.product_service.mapper;

import com.devwonder.product_service.dto.ProductVideoResponse;
import com.devwonder.product_service.entity.ProductVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductVideoMapper {
    
    @Mapping(target = "videoUrl", source = "url")
    @Mapping(target = "thumbnailUrl", source = "thumbnail")
    @Mapping(target = "displayOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ProductVideoResponse toResponse(ProductVideo video);
}