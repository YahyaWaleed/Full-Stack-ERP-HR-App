package com.yahya.erphrapp.employee.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// body of POST /employees/{id}/terminate -- both fields optional (date defaults to today)
public class TerminateEmployeeRequest {

    private LocalDate terminationDate;

    @Size(max = 200)
    private String reason;

    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
