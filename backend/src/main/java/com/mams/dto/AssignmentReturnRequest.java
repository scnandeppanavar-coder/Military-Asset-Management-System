package com.mams.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AssignmentReturnRequest {

    @NotNull(message = "Return quantity is required")
    @Positive(message = "Return quantity must be greater than zero")
    private Integer returnQuantity;

    public AssignmentReturnRequest() {}

    public AssignmentReturnRequest(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public Integer getReturnQuantity() { return returnQuantity; }
    public void setReturnQuantity(Integer returnQuantity) { this.returnQuantity = returnQuantity; }

    public static AssignmentReturnRequestBuilder builder() { return new AssignmentReturnRequestBuilder(); }

    public static class AssignmentReturnRequestBuilder {
        private Integer returnQuantity;

        public AssignmentReturnRequestBuilder returnQuantity(Integer returnQuantity) { this.returnQuantity = returnQuantity; return this; }

        public AssignmentReturnRequest build() {
            return new AssignmentReturnRequest(returnQuantity);
        }
    }
}
