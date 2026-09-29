package com.mams.service;

import com.mams.dto.ExpenditureRequest;
import com.mams.dto.ExpenditureResponse;
import com.mams.entity.*;
import com.mams.exception.InsufficientInventoryException;
import com.mams.repository.BaseRepository;
import com.mams.repository.EquipmentTypeRepository;
import com.mams.repository.ExpenditureRepository;
import com.mams.repository.InventoryMovementRepository;
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
class ExpenditureServiceTest {

    @Mock
    private ExpenditureRepository expenditureRepository;

    @Mock
    private BaseRepository baseRepository;

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ExpenditureService expenditureService;

    private Base alphaBase;
    private EquipmentType ammunition;
    private CustomUserDetails commanderUserDetails;
    private CustomUserDetails logisticsUserDetails;

    @BeforeEach
    void setUp() {
        alphaBase = Base.builder()
                .id(1L)
                .baseCode("BASE-ALPHA")
                .baseName("Fort Alpha Garrison")
                .status("ACTIVE")
                .build();

        ammunition = EquipmentType.builder()
                .id(5L)
                .name("5.56x45mm NATO Ball Ammunition")
                .category("Ammunition")
                .unit("Boxes")
                .build();

        User commanderUser = User.builder()
                .id(2L)
                .username("commander")
                .role(Role.BASE_COMMANDER)
                .base(alphaBase)
                .build();
        commanderUserDetails = new CustomUserDetails(commanderUser);

        User logisticsUser = User.builder()
                .id(3L)
                .username("logistics")
                .role(Role.LOGISTICS_OFFICER)
                .base(alphaBase)
                .build();
        logisticsUserDetails = new CustomUserDetails(logisticsUser);
    }

    @Test
    @DisplayName("Should successfully record expenditure and create outflow movement")
    void testCreateExpenditureSuccess() {
        ExpenditureRequest request = ExpenditureRequest.builder()
                .baseId(1L)
                .equipmentTypeId(5L)
                .quantity(30)
                .expenditureDate(LocalDate.now())
                .reason("Live-Fire Tactical Training")
                .referenceNumber("EXP-TEST-001")
                .build();

        when(baseRepository.findById(1L)).thenReturn(Optional.of(alphaBase));
        when(equipmentTypeRepository.findById(5L)).thenReturn(Optional.of(ammunition));
        when(movementRepository.calculateAvailableStock(1L, 5L)).thenReturn(200L); // 200 available > 30 requested
        when(expenditureRepository.existsByReferenceNumber("EXP-TEST-001")).thenReturn(false);

        Expenditure saved = Expenditure.builder()
                .id(10L)
                .base(alphaBase)
                .equipmentType(ammunition)
                .quantity(30)
                .expenditureDate(request.getExpenditureDate())
                .reason("Live-Fire Tactical Training")
                .referenceNumber("EXP-TEST-001")
                .recordedBy("commander")
                .build();

        when(expenditureRepository.save(any(Expenditure.class))).thenReturn(saved);

        ExpenditureResponse response = expenditureService.createExpenditure(request, commanderUserDetails);

        assertNotNull(response);
        assertEquals(30, response.getQuantity());
        verify(movementRepository).save(any(InventoryMovement.class));
        verify(auditLogService).log(eq("commander"), eq("EXPEND"), eq("EXPENDITURE"), eq(10L), anyString());
    }

    @Test
    @DisplayName("Should reject expenditure when requested quantity exceeds available stock")
    void testExpenditureInsufficientInventory() {
        ExpenditureRequest request = ExpenditureRequest.builder()
                .baseId(1L)
                .equipmentTypeId(5L)
                .quantity(500)
                .expenditureDate(LocalDate.now())
                .reason("Training")
                .build();

        when(baseRepository.findById(1L)).thenReturn(Optional.of(alphaBase));
        when(equipmentTypeRepository.findById(5L)).thenReturn(Optional.of(ammunition));
        when(movementRepository.calculateAvailableStock(1L, 5L)).thenReturn(100L); // only 100 available!

        assertThrows(InsufficientInventoryException.class, () ->
                expenditureService.createExpenditure(request, commanderUserDetails)
        );
        verify(expenditureRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should prevent Logistics Officer from recording expenditures (RBAC check)")
    void testLogisticsOfficerCannotRecordExpenditure() {
        ExpenditureRequest request = ExpenditureRequest.builder()
                .baseId(1L)
                .equipmentTypeId(5L)
                .quantity(10)
                .expenditureDate(LocalDate.now())
                .reason("Field use")
                .build();

        assertThrows(AccessDeniedException.class, () ->
                expenditureService.createExpenditure(request, logisticsUserDetails)
        );
        verify(expenditureRepository, never()).save(any());
    }
}
