package com.mams.controller;

import com.mams.dto.TransferRequest;
import com.mams.dto.TransferResponse;
import com.mams.security.CustomUserDetails;
import com.mams.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transfers", description = "Inter-base asset transfer and movement endpoints")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @GetMapping
    @Operation(summary = "Get all transfers", description = "Retrieves transfers filtered by source/destination base, equipment, and date")
    public ResponseEntity<List<TransferResponse>> getTransfers(
            @RequestParam(required = false) Long fromBaseId,
            @RequestParam(required = false) Long toBaseId,
            @RequestParam(required = false) Long equipmentTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(transferService.getTransfers(fromBaseId, toBaseId, equipmentTypeId, from, to, search, currentUser));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transfer by ID", description = "Retrieves transfer details by unique ID")
    public ResponseEntity<TransferResponse> getTransferById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(transferService.getTransferById(id, currentUser));
    }

    @PostMapping
    @Operation(summary = "Create transfer", description = "Executes an inter-base transfer, debiting source base and crediting destination base")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return new ResponseEntity<>(transferService.createTransfer(request, currentUser), HttpStatus.CREATED);
    }
}
