package com.devwonder.user_service.mapper;

import com.devwonder.user_service.dto.ResellerResponse;
import com.devwonder.user_service.entity.Reseller;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResellerMapper {
    
    ResellerResponse toResponse(Reseller reseller);
}