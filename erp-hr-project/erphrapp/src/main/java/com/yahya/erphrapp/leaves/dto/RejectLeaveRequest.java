package com.yahya.erphrapp.leaves.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RejectLeaveRequest {

    @NotBlank
    @Size(max = 200)
    private String rejectReason;

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
