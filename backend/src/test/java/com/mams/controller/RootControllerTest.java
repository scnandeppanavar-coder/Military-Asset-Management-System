package com.mams.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RootControllerTest {

    private final RootController rootController = new RootController();

    @Test
    void testRootEndpointReturnsSuccessMessage() {
        ResponseEntity<String> response = rootController.getRoot();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Military Asset Management System Backend is running", response.getBody());
    }
}
