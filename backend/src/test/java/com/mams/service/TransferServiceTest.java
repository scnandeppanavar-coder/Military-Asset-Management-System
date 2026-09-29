package com.mams.service;

import com.mams.dto.TransferRequest;
import com.mams.dto.TransferResponse;
import com.mams.entity.*;
import com.mams.exception.InsufficientInventoryException;
import com.mams.exception.InvalidOperationException;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.repository.TransferRepository;
import com.mams.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private BaseRepository baseRepository;

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TransferService transferService;

    private Base fromBase;
    private Base toBase;
    private EquipmentType equipmentType;
    private CustomUserDetails adminUserDetails;

    @BeforeEach
    void setUp() {
        fromBase = Base.builder()
                .id(1L)
                .baseCode("BASE-ALPHA")
                .baseName("Fort Alpha Garrison")
                .status("ACTIVE")
                .build();

        toBase = Base.builder()
                .id(2L)
                .baseCode("BASE-BRAVO")
                .baseName("Camp Bravo Forward Base")
                .status("ACTIVE")
                .build();

        equipmentType = EquipmentType.builder()
                .id(1L)
                .name("Tactical Utility Vehicle (4x4)")
                .category("Vehicle")
                .unit("Units")
                .build();

        User adminUser = User.builder()
                .id(1L)
                .username("admin")
                .role(Role.ADMIN)
                .build();
        adminUserDetails = new CustomUserDetails(adminUser);
    }

    @Test
    @DisplayName("Should execute transfer successfully and generate dual inventory movements")
    void testSuccessfulTransfer() {
        TransferRequest request = TransferRequest.builder()
                .fromBaseId(1L)
                .toBaseId(2L)
                .equipmentTypeId(1L)
                .quantity(5)
                .transferDate(LocalDate.now())
                .referenceNumber("TRF-TEST-001")
                .remarks("Inter-base transport reallocation")
                .build();

        when(baseRepository.findById(1L)).thenReturn(Optional.of(fromBase));
        when(baseRepository.findById(2L)).thenReturn(Optional.of(toBase));
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(equipmentType));
        when(movementRepository.calculateAvailableStock(1L, 1L)).thenReturn(50L); // 50 available > 5 requested
        when(transferRepository.existsByReferenceNumber("TRF-TEST-001")).thenReturn(false);

        Transfer savedTransfer = Transfer.builder()
                .id(100L)
                .fromBase(fromBase)
                .toBase(toBase)
                .equipmentType(equipmentType)
                .quantity(5)
                .transferDate(request.getTransferDate())
                .referenceNumber("TRF-TEST-001")
                .status("COMPLETED")
                .createdBy("admin")
                .build();

        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        TransferResponse response = transferService.createTransfer(request, adminUserDetails);

        assertNotNull(response);
        assertEquals(5, response.getQuantity());
        assertEquals("COMPLETED", response.getStatus());

        // Verify dual inventory movements (TRANSFER_OUT on source, TRANSFER_IN on destination)
        verify(movementRepository, times(2)).save(any(InventoryMovement.class));
        verify(auditLogService).log(eq("admin"), eq("TRANSFER"), eq("TRANSFER"), eq(100L), anyString());
    }

    @Test
    @DisplayName("Should reject transfer when source base and destination base are identical")
    void testTransferSameBaseRejected() {
        TransferRequest request = TransferRequest.builder()
                .fromBaseId(1L)
                .toBaseId(1L) // same base
                .equipmentTypeId(1L)
                .quantity(5)
                .transferDate(LocalDate.now())
                .build();

        assertThrows(InvalidOperationException.class, () ->
                transferService.createTransfer(request, adminUserDetails)
        );
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InsufficientInventoryException when requested quantity exceeds available stock")
    void testInsufficientInventoryTransferRejected() {
        TransferRequest request = TransferRequest.builder()
                .fromBaseId(1L)
                .toBaseId(2L)
                .equipmentTypeId(1L)
                .quantity(100) // requesting 100
                .transferDate(LocalDate.now())
                .build();

        when(baseRepository.findById(1L)).thenReturn(Optional.of(fromBase));
        when(baseRepository.findById(2L)).thenReturn(Optional.of(toBase));
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(equipmentType));
        when(movementRepository.calculateAvailableStock(1L, 1L)).thenReturn(10L); // only 10 available!

        InsufficientInventoryException exception = assertThrows(
                InsufficientInventoryException.class,
                () -> transferService.createTransfer(request, adminUserDetails)
        );

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
        verify(transferRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }
}
