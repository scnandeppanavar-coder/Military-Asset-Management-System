package com.mams.controller;

import com.mams.dto.PurchaseRequest;
import com.mams.dto.PurchaseResponse;
import com.mams.security.CustomUserDetails;
import com.mams.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Purchases", description = "Equipment procurement and acquisition endpoints")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    @Operation(summary = "Get all purchases", description = "Retrieves purchases filtered by base, equipment, and date")
    public ResponseEntity<List<PurchaseResponse>> getPurchases(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(purchaseService.getPurchases(baseId, equipmentTypeId, from, to, search, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase by ID", description = "Retrieves purchase details by unique ID")
    public ResponseEntity<PurchaseResponse> getPurchaseById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id, currentUser));
    }

    @PostMapping
    @Operation(summary = "Create purchase", description = "Records a new asset purchase and creates inventory inflow movement")
    public ResponseEntity<PurchaseResponse> createPurchase(
            @Valid @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return new ResponseEntity<>(purchaseService.createPurchase(request, currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    @Operation(summary = "Update purchase", description = "Modifies purchase details and synchronizes inventory movements")
    public ResponseEntity<PurchaseResponse> updatePurchase(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(purchaseService.updatePurchase(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
    @Operation(summary = "Delete purchase", description = "Deletes a purchase record if inventory allows")
    public ResponseEntity<Void> deletePurchase(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        purchaseService.deletePurchase(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
