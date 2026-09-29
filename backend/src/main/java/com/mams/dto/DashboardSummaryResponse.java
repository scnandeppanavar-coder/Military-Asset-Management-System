package com.mams.dto;

import java.time.LocalDate;
import java.util.List;

public class DashboardSummaryResponse {
    private Long openingBalance;
    private Long purchases;
    private Long transferIn;
    private Long transferOut;
    private Long netMovement;
    private Long expended;
    private Long closingBalance;
    private Long assigned;

    private Long baseId;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private LocalDate from;
    private LocalDate to;

    private List<MovementItemDTO> recentMovements;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(Long openingBalance, Long purchases, Long transferIn, Long transferOut, Long netMovement, Long expended, Long closingBalance, Long assigned, Long baseId, String baseName, Long equipmentTypeId, String equipmentName, LocalDate from, LocalDate to, List<MovementItemDTO> recentMovements) {
        this.openingBalance = openingBalance;
        this.purchases = purchases;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
        this.netMovement = netMovement;
        this.expended = expended;
        this.closingBalance = closingBalance;
        this.assigned = assigned;
        this.baseId = baseId;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.from = from;
        this.to = to;
        this.recentMovements = recentMovements;
    }

    public Long getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Long openingBalance) { this.openingBalance = openingBalance; }

    public Long getPurchases() { return purchases; }
    public void setPurchases(Long purchases) { this.purchases = purchases; }

    public Long getTransferIn() { return transferIn; }
    public void setTransferIn(Long transferIn) { this.transferIn = transferIn; }

    public Long getTransferOut() { return transferOut; }
    public void setTransferOut(Long transferOut) { this.transferOut = transferOut; }

    public Long getNetMovement() { return netMovement; }
    public void setNetMovement(Long netMovement) { this.netMovement = netMovement; }

    public Long getExpended() { return expended; }
    public void setExpended(Long expended) { this.expended = expended; }

    public Long getClosingBalance() { return closingBalance; }
    public void setClosingBalance(Long closingBalance) { this.closingBalance = closingBalance; }

    public Long getAssigned() { return assigned; }
    public void setAssigned(Long assigned) { this.assigned = assigned; }

    public Long getBaseId() { return baseId; }
    public void setBaseId(Long baseId) { this.baseId = baseId; }

    public String getBaseName() { return baseName; }
    public void setBaseName(String baseName) { this.baseName = baseName; }

    public Long getEquipmentTypeId() { return equipmentTypeId; }
    public void setEquipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }

    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }

    public List<MovementItemDTO> getRecentMovements() { return recentMovements; }
    public void setRecentMovements(List<MovementItemDTO> recentMovements) { this.recentMovements = recentMovements; }

    public static DashboardSummaryResponseBuilder builder() { return new DashboardSummaryResponseBuilder(); }

    public static class DashboardSummaryResponseBuilder {
        private Long openingBalance;
        private Long purchases;
        private Long transferIn;
        private Long transferOut;
        private Long netMovement;
        private Long expended;
        private Long closingBalance;
        private Long assigned;
        private Long baseId;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private LocalDate from;
        private LocalDate to;
        private List<MovementItemDTO> recentMovements;

        public DashboardSummaryResponseBuilder openingBalance(Long openingBalance) { this.openingBalance = openingBalance; return this; }
        public DashboardSummaryResponseBuilder purchases(Long purchases) { this.purchases = purchases; return this; }
        public DashboardSummaryResponseBuilder transferIn(Long transferIn) { this.transferIn = transferIn; return this; }
        public DashboardSummaryResponseBuilder transferOut(Long transferOut) { this.transferOut = transferOut; return this; }
        public DashboardSummaryResponseBuilder netMovement(Long netMovement) { this.netMovement = netMovement; return this; }
        public DashboardSummaryResponseBuilder expended(Long expended) { this.expended = expended; return this; }
        public DashboardSummaryResponseBuilder closingBalance(Long closingBalance) { this.closingBalance = closingBalance; return this; }
        public DashboardSummaryResponseBuilder assigned(Long assigned) { this.assigned = assigned; return this; }
        public DashboardSummaryResponseBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public DashboardSummaryResponseBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public DashboardSummaryResponseBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public DashboardSummaryResponseBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public DashboardSummaryResponseBuilder from(LocalDate from) { this.from = from; return this; }
        public DashboardSummaryResponseBuilder to(LocalDate to) { this.to = to; return this; }
        public DashboardSummaryResponseBuilder recentMovements(List<MovementItemDTO> recentMovements) { this.recentMovements = recentMovements; return this; }

        public DashboardSummaryResponse build() {
            return new DashboardSummaryResponse(openingBalance, purchases, transferIn, transferOut, netMovement, expended, closingBalance, assigned, baseId, baseName, equipmentTypeId, equipmentName, from, to, recentMovements);
        }
    }
}
