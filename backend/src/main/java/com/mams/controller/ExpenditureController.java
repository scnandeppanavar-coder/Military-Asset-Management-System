package com.mams.controller;

import com.mams.dto.ExpenditureRequest;
import com.mams.dto.ExpenditureResponse;
import com.mams.security.CustomUserDetails;
import com.mams.service.ExpenditureService;
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
@RequestMapping("/api/expenditures")
@PreAuthorize("hasAnyRole('ADMIN', 'BASE_COMMANDER')")
@Tag(name = "Expenditures", description = "Asset expenditure and consumption tracking endpoints")
public class ExpenditureController {

    private final ExpenditureService expenditureService;

    public ExpenditureController(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @GetMapping
    @Operation(summary = "Get all expenditures", description = "Retrieves recorded asset expenditures filtered by base, equipment, and date")
    public ResponseEntity<List<ExpenditureResponse>> getExpenditures(
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(expenditureService.getExpenditures(baseId, equipmentTypeId, from, to, search, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get expenditure by ID", description = "Retrieves expenditure details by unique ID")
    public ResponseEntity<ExpenditureResponse> getExpenditureById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(expenditureService.getExpenditureById(id, currentUser));
    }

    @PostMapping
    @Operation(summary = "Record expenditure", description = "Records asset consumption/expenditure and creates inventory outflow movement")
    public ResponseEntity<ExpenditureResponse> createExpenditure(
            @Valid @RequestBody ExpenditureRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return new ResponseEntity<>(expenditureService.createExpenditure(request, currentUser), HttpStatus.CREATED);
    }
}
