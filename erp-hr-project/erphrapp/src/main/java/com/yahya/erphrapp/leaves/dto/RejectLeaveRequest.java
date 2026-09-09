package com.yahya.erphrapp.leaves.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectLeaveRequest {

    @NotBlank
    private String rejectReason;

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}