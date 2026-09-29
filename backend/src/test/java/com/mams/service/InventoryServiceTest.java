package com.mams.service;

import com.mams.dto.DashboardSummaryResponse;
import com.mams.dto.NetMovementBreakdownResponse;
import com.mams.entity.Base;
import com.mams.entity.EquipmentType;
import com.mams.entity.MovementType;
import com.mams.entity.Role;
import com.mams.entity.User;
import com.mams.repository.AssignmentRepository;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.InventoryMovementRepository;
import com.mams.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private BaseRepository baseRepository;

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Base alphaBase;
    private EquipmentType tacticalVehicle;
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

        tacticalVehicle = EquipmentType.builder()
                .id(1L)
                .name("Tactical Utility Vehicle")
                .category("Vehicle")
                .unit("Units")
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
    @DisplayName("Should correctly calculate Opening Balance, Net Movement, and Closing Balance")
    void testDashboardCalculations() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);

        // Opening balance before Sep 1 = 1250
        when(movementRepository.calculateOpeningBalance(1L, 1L, from)).thenReturn(1250L);

        // In period: Purchases = 200, Transfer In = 50, Transfer Out = 70 -> Net Movement = 180
        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.PURCHASE, from, to)).thenReturn(200L);
        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.TRANSFER_IN, from, to)).thenReturn(50L);
        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.TRANSFER_OUT, from, to)).thenReturn(70L);

        // Expended = 50 -> Closing Balance = 1250 + 180 - 50 = 1380
        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.EXPENDITURE, from, to)).thenReturn(50L);

        // Active Assigned = 25
        when(assignmentRepository.calculateCurrentlyAssigned(1L, 1L, from, to)).thenReturn(25L);

        when(baseRepository.findById(1L)).thenReturn(Optional.of(alphaBase));
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(tacticalVehicle));
        when(movementRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        DashboardSummaryResponse summary = inventoryService.getDashboardSummary(
                1L, 1L, from, to, adminUserDetails
        );

        assertNotNull(summary);
        assertEquals(1250L, summary.getOpeningBalance(), "Opening balance must match historical movements before start date");
        assertEquals(200L, summary.getPurchases());
        assertEquals(50L, summary.getTransferIn());
        assertEquals(70L, summary.getTransferOut());
        assertEquals(180L, summary.getNetMovement(), "Net movement must equal Purchases + Transfer In - Transfer Out");
        assertEquals(50L, summary.getExpended());
        assertEquals(1380L, summary.getClosingBalance(), "Closing balance must equal Opening + Net Movement - Expended");
        assertEquals(25L, summary.getAssigned());
    }

    @Test
    @DisplayName("Should prevent Base Commander from viewing another base dashboard")
    void testBaseCommanderCrossBaseAccessDenied() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);

        // Commander is assigned to Base 1 (Fort Alpha). Attempting to request Base 2 (Bravo)
        assertThrows(AccessDeniedException.class, () ->
                inventoryService.getDashboardSummary(2L, null, from, to, commanderUserDetails)
        );
    }

    @Test
    @DisplayName("Should calculate Net Movement breakdown correctly for popup modal")
    void testNetMovementBreakdown() {
        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);

        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.PURCHASE, from, to)).thenReturn(100L);
        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.TRANSFER_IN, from, to)).thenReturn(40L);
        when(movementRepository.sumQuantityByMovementType(1L, 1L, MovementType.TRANSFER_OUT, from, to)).thenReturn(30L);
        when(baseRepository.findById(1L)).thenReturn(Optional.of(alphaBase));
        when(equipmentTypeRepository.findById(1L)).thenReturn(Optional.of(tacticalVehicle));

        NetMovementBreakdownResponse breakdown = inventoryService.getNetMovementBreakdown(
                1L, 1L, from, to, adminUserDetails
        );

        assertNotNull(breakdown);
        assertEquals(100L, breakdown.getPurchases());
        assertEquals(40L, breakdown.getTransferIn());
        assertEquals(30L, breakdown.getTransferOut());
        assertEquals(110L, breakdown.getNetMovement(), "Net Movement must be 100 + 40 - 30 = 110");
    }
}
