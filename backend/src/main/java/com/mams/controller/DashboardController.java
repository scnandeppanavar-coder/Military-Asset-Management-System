package com.mams.controller;

import com.mams.dto.DashboardSummaryResponse;
import com.mams.dto.NetMovementBreakdownResponse;
import com.mams.security.CustomUserDetails;
import com.mams.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard analytics, opening/closing balance, and net movement breakdown")
public class DashboardController {

    private final InventoryService inventoryService;

    public DashboardController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "Get dashboard analytics summary",
            description = "Calculates Opening Balance, Net Movement (Purchases + Transfer In - Transfer Out), Closing Balance, and Assigned stock based on filters")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(inventoryService.getDashboardSummary(baseId, equipmentTypeId, from, to, currentUser));
    }

    @GetMapping("/net-movement")
    @Operation(summary = "Get net movement breakdown modal details",
            description = "Provides itemized breakdown of Purchases, Transfer In, and Transfer Out for popup card inspection")
    public ResponseEntity<NetMovementBreakdownResponse> getNetMovementBreakdown(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(inventoryService.getNetMovementBreakdown(baseId, equipmentTypeId, from, to, currentUser));
    }
}
