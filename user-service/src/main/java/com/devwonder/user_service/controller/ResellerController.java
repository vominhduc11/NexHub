package com.devwonder.user_service.controller;

import com.devwonder.user_service.entity.Reseller;
import com.devwonder.user_service.service.ResellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/resellers")
@RequiredArgsConstructor
@Tag(name = "Reseller Management", description = "Operations related to reseller management")
@SecurityRequirement(name = "Gateway Request")
@SecurityRequirement(name = "JWT Authentication")
public class ResellerController {

    private final ResellerService resellerService;

    @GetMapping
    public ResponseEntity<List<Reseller>> getAllResellers() {
        List<Reseller> resellers = resellerService.getAllResellers();
        return ResponseEntity.ok(resellers);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<Reseller> getResellerByAccountId(@PathVariable Long accountId) {
        Optional<Reseller> reseller = resellerService.getResellerByAccountId(accountId);
        return reseller.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Reseller> getResellerByEmail(@PathVariable String email) {
        Optional<Reseller> reseller = resellerService.getResellerByEmail(email);
        return reseller.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<Reseller> getResellerByPhone(@PathVariable String phone) {
        Optional<Reseller> reseller = resellerService.getResellerByPhone(phone);
        return reseller.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Reseller> createReseller(@RequestBody Reseller reseller) {
        try {
            Reseller createdReseller = resellerService.createReseller(reseller);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReseller);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<Reseller> updateReseller(@PathVariable Long accountId, @RequestBody Reseller resellerDetails) {
        try {
            Reseller updatedReseller = resellerService.updateReseller(accountId, resellerDetails);
            return ResponseEntity.ok(updatedReseller);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteReseller(@PathVariable Long accountId) {
        try {
            resellerService.deleteReseller(accountId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{accountId}/exists")
    public ResponseEntity<Boolean> checkResellerExists(@PathVariable Long accountId) {
        boolean exists = resellerService.existsByAccountId(accountId);
        return ResponseEntity.ok(exists);
    }
}