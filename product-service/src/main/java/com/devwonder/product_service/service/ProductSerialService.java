package com.devwonder.product_service.service;

import com.devwonder.product_service.repository.ProductSerialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSerialService {
    
    private final ProductSerialRepository productSerialRepository;
    
    public boolean existsById(Long id) {
        return productSerialRepository.existsById(id);
    }
}