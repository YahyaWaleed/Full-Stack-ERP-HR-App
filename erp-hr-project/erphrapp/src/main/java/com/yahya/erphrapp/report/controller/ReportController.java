package com.yahya.erphrapp.report.controller;

import com.yahya.erphrapp.report.dto.*;
import com.yahya.erphrapp.report.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yahya.erphrapp.report.dto.EmployeeDirectoryResponse;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/employee-directory")
    public List<EmployeeDirectoryResponse> getEmployeeDirectory() {
        return reportService.getEmployeeDirectory();
    }

    @GetMapping("/headcount-by-department")
    public List<HeadcountByDeptResponse> getHeadcountByDept() {
        return reportService.getHeadcountByDept();
    }

    @GetMapping("/payroll-register")
    public List<PayrollRegisterResponse> getPayrollRegister(@RequestParam String periodCode) {
        return reportService.getPayrollRegister(periodCode);
    }

    @GetMapping("/payroll-cost-by-department")
    public List<PayrollCostByDeptResponse> getPayrollCostByDept(@RequestParam String periodCode) {
        return reportService.getPayrollCostByDept(periodCode);
    }

    @GetMapping("/payroll-trend")
    public List<PayrollTrendResponse> getPayrollTrend() {
        return reportService.getPayrollTrend();
    }

    @GetMapping("/tax-insurance-liability")
    public List<TaxInsuranceLiabilityResponse> getTaxInsuranceLiability() {
        return reportService.getTaxInsuranceLiability();
    }

    @GetMapping("/bank-transfer")
    public List<BankTransferResponse> getBankTransfer(@RequestParam String periodCode) {
        return reportService.getBankTransfer(periodCode);
    }

    @GetMapping("/leave-balances")
    public List<LeaveBalanceReportResponse> getLeaveBalances(@RequestParam int fiscalYear) {
        return reportService.getLeaveBalances(fiscalYear);
    }

    @GetMapping("/leave-requests")
    public List<LeaveRequestLogResponse> getLeaveRequestLog(@RequestParam(required = false) String status) {
        return reportService.getLeaveRequestLog(status);
    }

    @GetMapping("/overtime-top10")
    public List<OvertimeTopResponse> getOvertimeTop10() {
        return reportService.getOvertimeTop10();
    }

    @GetMapping("/absence-watchlist")
    public List<AbsenceWatchlistResponse> getAbsenceWatchlist() {
        return reportService.getAbsenceWatchlist();
    }

    @GetMapping("/active-loans")
    public List<ActiveLoanResponse> getActiveLoans() {
        return reportService.getActiveLoans();
    }

    @GetMapping("/contracts-expiring")
    public List<ContractExpiringResponse> getContractsExpiring(@RequestParam(defaultValue = "12") int months) {
        return reportService.getContractsExpiring(months);
    }

    @GetMapping("/top-attendance")
    public List<TopAttendanceResponse> getTopAttendance(@RequestParam String periodCode) {
        return reportService.getTopAttendance(periodCode);
    }

    @GetMapping("/top-net-salary")
    public List<TopNetSalaryResponse> getTopNetSalary(@RequestParam String periodCode) {
        return reportService.getTopNetSalary(periodCode);
    }
}