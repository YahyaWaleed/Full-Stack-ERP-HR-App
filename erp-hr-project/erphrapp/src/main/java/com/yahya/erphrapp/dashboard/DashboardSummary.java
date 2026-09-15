package com.yahya.erphrapp.dashboard;

public class DashboardSummary {
    private final long employeesCount;
    private final long pendingLeaves;
    private final long activeLoans;

    public DashboardSummary(long employeesCount, long pendingLeaves, long activeLoans) {
        this.employeesCount = employeesCount;
        this.pendingLeaves = pendingLeaves;
        this.activeLoans = activeLoans;
    }

    public long getEmployeesCount() { return employeesCount; }
    public long getPendingLeaves() { return pendingLeaves; }
    public long getActiveLoans() { return activeLoans; }
}