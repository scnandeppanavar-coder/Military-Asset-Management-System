package com.mams.service;

import com.mams.dto.PurchaseRequest;
import com.mams.dto.PurchaseResponse;
import com.mams.entity.*;
import com.mams.exception.InsufficientInventoryException;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.repository.PurchaseRepository;
import com.mams.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private BaseRepository baseRepository;

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PurchaseService purchaseService;

    private Base alphaBase;
    private EquipmentType assaultRifle;
    private CustomUserDetails adminUserDetails;
    private CustomUserDetails commanderUserDetails;

    @BeforeEach
    void setUp() {
        alphaBase = Base.builder()
                .id(1L)
                .baseCode("BASE-ALPHA")
                .baseName("Fort Alpha Garrison")
                .status("ACTIVE")
                .build();

        assaultRifle = EquipmentType.builder()
                .id(3L)
                .name("5.56mm Standard Infantry Service Rifle")
                .category("Weapon")
                .unit("Pieces")
                .build();

        User adminUser = User.builder()
                .id(1L)
                .username("admin")
                .role(Role.ADMIN)
                .build();
        adminUserDetails = new CustomUserDetails(adminUser);

        User commanderUser = User.builder()
                .id(2L)
                .username("commander")
                .role(Role.BASE_COMMANDER)
                .base(alphaBase)
                .build();
        commanderUserDetails = new CustomUserDetails(commanderUser);
    }

    @Test
    @DisplayName("Should create purchase and record inventory inflow movement")
    void testCreatePurchase() {
        PurchaseRequest request = PurchaseRequest.builder()
                .baseId(1L)
                .equipmentTypeId(3L)
                .quantity(150)
                .purchaseDate(LocalDate.now())
                .referenceNumber("PUR-TEST-001")
                .remarks("Batch acquisition")
                .build();

        when(baseRepository.findById(1L)).thenReturn(Optional.of(alphaBase));
        when(equipmentTypeRepository.findById(3L)).thenReturn(Optional.of(assaultRifle));
        when(purchaseRepository.existsByReferenceNumber("PUR-TEST-001")).thenReturn(false);

        Purchase savedPurchase = Purchase.builder()
                .id(50L)
                .base(alphaBase)
                .equipmentType(assaultRifle)
                .quantity(150)
                .purchaseDate(request.getPurchaseDate())
                .referenceNumber("PUR-TEST-001")
                .remarks("Batch acquisition")
                .createdBy("admin")
                .build();

        when(purchaseRepository.save(any(Purchase.class))).thenReturn(savedPurchase);

        PurchaseResponse response = purchaseService.createPurchase(request, adminUserDetails);

        assertNotNull(response);
        assertEquals(150, response.getQuantity());
        assertEquals("PUR-TEST-001", response.getReferenceNumber());
        verify(movementRepository).save(any(InventoryMovement.class));
        verify(auditLogService).log(eq("admin"), eq("PURCHASE"), eq("PURCHASE"), eq(50L), anyString());
    }

    @Test
    @DisplayName("Should prevent Base Commander from purchasing for another base")
    void testCommanderCannotPurchaseForOtherBase() {
        PurchaseRequest request = PurchaseRequest.builder()
                .baseId(2L) // Bravo Base
                .equipmentTypeId(3L)
                .quantity(50)
                .purchaseDate(LocalDate.now())
                .build();

        assertThrows(AccessDeniedException.class, () ->
                purchaseService.createPurchase(request, commanderUserDetails)
        );
        verify(purchaseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should prevent deletion of purchase if available inventory is insufficient")
    void testDeletePurchaseInsufficientInventory() {
        Purchase purchase = Purchase.builder()
                .id(1L)
                .base(alphaBase)
                .equipmentType(assaultRifle)
                .quantity(100)
                .referenceNumber("PUR-001")
                .build();

        when(purchaseRepository.findById(1L)).thenReturn(Optional.of(purchase));
        // Only 20 available in stock because 80 were already transferred or expended
        when(movementRepository.calculateAvailableStock(1L, 3L)).thenReturn(20L);

        assertThrows(InsufficientInventoryException.class, () ->
                purchaseService.deletePurchase(1L, adminUserDetails)
        );
        verify(purchaseRepository, never()).delete(any());
    }
}
