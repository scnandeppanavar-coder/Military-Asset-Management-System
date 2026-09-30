package com.mams.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Root", description = "Root health and status endpoint")
public class RootController {

    @GetMapping("/")
    @Operation(summary = "Root status", description = "Returns system running status message")
    public ResponseEntity<String> getRoot() {
        return ResponseEntity.ok("Military Asset Management System Backend is running");
    }
}
