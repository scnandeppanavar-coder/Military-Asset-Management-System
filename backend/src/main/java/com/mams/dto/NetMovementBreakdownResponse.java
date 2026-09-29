package com.mams.dto;

import java.time.LocalDate;

public class NetMovementBreakdownResponse {
    private Long purchases;
    private Long transferIn;
    private Long transferOut;
    private Long netMovement;

    private Long baseId;
    private String baseName;
    private Long equipmentTypeId;
    private String equipmentName;
    private LocalDate from;
    private LocalDate to;

    public NetMovementBreakdownResponse() {}

    public NetMovementBreakdownResponse(Long purchases, Long transferIn, Long transferOut, Long netMovement, Long baseId, String baseName, Long equipmentTypeId, String equipmentName, LocalDate from, LocalDate to) {
        this.purchases = purchases;
        this.transferIn = transferIn;
        this.transferOut = transferOut;
        this.netMovement = netMovement;
        this.baseId = baseId;
        this.baseName = baseName;
        this.equipmentTypeId = equipmentTypeId;
        this.equipmentName = equipmentName;
        this.from = from;
        this.to = to;
    }

    public Long getPurchases() { return purchases; }
    public void setPurchases(Long purchases) { this.purchases = purchases; }

    public Long getTransferIn() { return transferIn; }
    public void setTransferIn(Long transferIn) { this.transferIn = transferIn; }

    public Long getTransferOut() { return transferOut; }
    public void setTransferOut(Long transferOut) { this.transferOut = transferOut; }

    public Long getNetMovement() { return netMovement; }
    public void setNetMovement(Long netMovement) { this.netMovement = netMovement; }

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

    public static NetMovementBreakdownResponseBuilder builder() { return new NetMovementBreakdownResponseBuilder(); }

    public static class NetMovementBreakdownResponseBuilder {
        private Long purchases;
        private Long transferIn;
        private Long transferOut;
        private Long netMovement;
        private Long baseId;
        private String baseName;
        private Long equipmentTypeId;
        private String equipmentName;
        private LocalDate from;
        private LocalDate to;

        public NetMovementBreakdownResponseBuilder purchases(Long purchases) { this.purchases = purchases; return this; }
        public NetMovementBreakdownResponseBuilder transferIn(Long transferIn) { this.transferIn = transferIn; return this; }
        public NetMovementBreakdownResponseBuilder transferOut(Long transferOut) { this.transferOut = transferOut; return this; }
        public NetMovementBreakdownResponseBuilder netMovement(Long netMovement) { this.netMovement = netMovement; return this; }
        public NetMovementBreakdownResponseBuilder baseId(Long baseId) { this.baseId = baseId; return this; }
        public NetMovementBreakdownResponseBuilder baseName(String baseName) { this.baseName = baseName; return this; }
        public NetMovementBreakdownResponseBuilder equipmentTypeId(Long equipmentTypeId) { this.equipmentTypeId = equipmentTypeId; return this; }
        public NetMovementBreakdownResponseBuilder equipmentName(String equipmentName) { this.equipmentName = equipmentName; return this; }
        public NetMovementBreakdownResponseBuilder from(LocalDate from) { this.from = from; return this; }
        public NetMovementBreakdownResponseBuilder to(LocalDate to) { this.to = to; return this; }

        public NetMovementBreakdownResponse build() {
            return new NetMovementBreakdownResponse(purchases, transferIn, transferOut, netMovement, baseId, baseName, equipmentTypeId, equipmentName, from, to);
        }
    }
}
