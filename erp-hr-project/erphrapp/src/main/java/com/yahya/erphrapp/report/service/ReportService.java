package com.yahya.erphrapp.report.service;

import com.yahya.erphrapp.report.dto.*;
//import com.yahya.erphrapp.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import  com.yahya.erphrapp.report.repository.ReportRepository;

import com.yahya.erphrapp.report.dto.EmployeeDirectoryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<EmployeeDirectoryResponse> getEmployeeDirectory() {
        return reportRepository.getEmployeeDirectory().stream().map(r -> {
            EmployeeDirectoryResponse d = new EmployeeDirectoryResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setFullNameEn((String) r[2]);
            d.setGender(String.valueOf(r[3]));
            d.setAge(((Number) r[4]).intValue());
            d.setDepartment((String) r[5]);
            d.setJobTitle((String) r[6]);
            d.setJobGrade((String) r[7]);
            d.setBranch((String) r[8]);
            d.setManager((String) r[9]);
            d.setHireDate((LocalDate) r[10]);
            d.setYearsOfService(((Number) r[11]).intValue());
            d.setEmpStatus((String) r[12]);
            d.setContractType((String) r[13]);
            d.setBasicSalary((BigDecimal) r[14]);
            d.setEmail((String) r[15]);
            d.setMobile((String) r[16]);
            return d;
        }).toList();
    }

    public List<HeadcountByDeptResponse> getHeadcountByDept() {
        return reportRepository.getHeadcountByDept().stream().map(r -> {
            HeadcountByDeptResponse d = new HeadcountByDeptResponse();
            d.setCode((String) r[0]);
            d.setDepartment((String) r[1]);
            d.setBranch((String) r[2]);
            d.setHeadcount(((Number) r[3]).longValue());
            d.setMales(((Number) r[4]).longValue());
            d.setFemales(((Number) r[5]).longValue());
            d.setAvgServiceYears((BigDecimal) r[6]);
            d.setAvgBasicSalary((BigDecimal) r[7]);
            return d;
        }).toList();
    }

    public List<PayrollRegisterResponse> getPayrollRegister(String periodCode) {
        return reportRepository.getPayrollRegister(periodCode).stream().map(r -> {
            PayrollRegisterResponse d = new PayrollRegisterResponse();
            d.setPayslipId(((Number) r[0]).longValue());
            d.setPayslipNo((String) r[1]);
            d.setPeriodCode((String) r[2]);
            d.setPayDate((LocalDate) r[3]);;
            d.setEmpCode((String) r[4]);
            d.setFullNameAr((String) r[5]);
            d.setDepartment((String) r[6]);
            d.setJobTitle((String) r[7]);
            d.setBasicSalary((BigDecimal) r[8]);
            d.setGrossPay((BigDecimal) r[9]);
            d.setTotalDeductions((BigDecimal) r[10]);
            d.setInsuranceEmployee((BigDecimal) r[11]);
            d.setIncomeTax((BigDecimal) r[12]);
            d.setNetPay((BigDecimal) r[13]);
            d.setWorkedDays((BigDecimal) r[14]);
            d.setAbsentDays((BigDecimal) r[15]);
            d.setOvertimeHours((BigDecimal) r[16]);
            d.setInsuranceEmployer((BigDecimal) r[17]);
            d.setTotalCompanyCost((BigDecimal) r[18]);
            d.setStatus((String) r[19]);
            d.setCurrency((String) r[20]);
            return d;
        }).toList();
    }

    public List<PayrollCostByDeptResponse> getPayrollCostByDept(String periodCode) {
        return reportRepository.getPayrollCostByDept(periodCode).stream().map(r -> {
            PayrollCostByDeptResponse d = new PayrollCostByDeptResponse();
            d.setPeriodCode((String) r[0]);
            d.setDepartment((String) r[1]);
            d.setEmployees(((Number) r[2]).longValue());
            d.setTotalBasic((BigDecimal) r[3]);
            d.setTotalGross((BigDecimal) r[4]);
            d.setTotalTax((BigDecimal) r[5]);
            d.setInsuranceEmployee((BigDecimal) r[6]);
            d.setInsuranceEmployer((BigDecimal) r[7]);
            d.setTotalNet((BigDecimal) r[8]);
            d.setCompanyCost((BigDecimal) r[9]);
            return d;
        }).toList();
    }

    public List<PayrollTrendResponse> getPayrollTrend() {
        return reportRepository.getPayrollTrend().stream().map(r -> {
            PayrollTrendResponse d = new PayrollTrendResponse();
            d.setPeriodCode((String) r[0]);
            d.setEmployees(((Number) r[1]).longValue());
            d.setGross((BigDecimal) r[2]);
            d.setDeductions((BigDecimal) r[3]);
            d.setNet((BigDecimal) r[4]);
            d.setCompanyCost((BigDecimal) r[5]);
            return d;
        }).toList();
    }

    public List<TaxInsuranceLiabilityResponse> getTaxInsuranceLiability() {
        return reportRepository.getTaxInsuranceLiability().stream().map(r -> {
            TaxInsuranceLiabilityResponse d = new TaxInsuranceLiabilityResponse();
            d.setPeriodCode((String) r[0]);
            d.setIncomeTaxDue((BigDecimal) r[1]);
            d.setInsuranceEmployeeShare((BigDecimal) r[2]);
            d.setInsuranceEmployerShare((BigDecimal) r[3]);
            d.setTotalInsuranceDue((BigDecimal) r[4]);
            return d;
        }).toList();
    }

    public List<BankTransferResponse> getBankTransfer(String periodCode) {
        return reportRepository.getBankTransfer(periodCode).stream().map(r -> {
            BankTransferResponse d = new BankTransferResponse();
            d.setFullNameEn((String) r[0]);
            d.setBankName((String) r[1]);
            d.setBankAccount((String) r[2]);
            d.setAmount((BigDecimal) r[3]);
            d.setReference((String) r[4]);
            d.setPaidOn((LocalDate) r[5]);;
            return d;
        }).toList();
    }

    public List<LeaveBalanceReportResponse> getLeaveBalances(int fiscalYear) {
        return reportRepository.getLeaveBalances(fiscalYear).stream().map(r -> {
            LeaveBalanceReportResponse d = new LeaveBalanceReportResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setDepartment((String) r[2]);
            d.setLeaveType((String) r[3]);
            d.setFiscalYear(((Number) r[4]).intValue());
            d.setEntitledDays((BigDecimal) r[5]);
            d.setCarriedForward((BigDecimal) r[6]);
            d.setUsedDays((BigDecimal) r[7]);
            d.setRemainingDays((BigDecimal) r[8]);
            return d;
        }).toList();
    }

    public List<LeaveRequestLogResponse> getLeaveRequestLog(String status) {
        return reportRepository.getLeaveRequestLog(status).stream().map(r -> {
            LeaveRequestLogResponse d = new LeaveRequestLogResponse();
            d.setRequestId(((Number) r[0]).longValue());
            d.setEmpCode((String) r[1]);
            d.setFullNameAr((String) r[2]);
            d.setLeaveType((String) r[3]);
            d.setStartDate((LocalDate) r[4]);;
            d.setEndDate((LocalDate) r[5]);;
            d.setDaysCount((BigDecimal) r[6]);
            d.setStatus((String) r[7]);
            d.setApprovedBy((String) r[8]);
            d.setAppliedOn((LocalDate) r[9]);;
            d.setDecidedOn(r[10] != null ? (LocalDate) r[10] : null);;
            d.setReason((String) r[11]);
            return d;
        }).toList();
    }

    public List<OvertimeTopResponse> getOvertimeTop10() {
        return reportRepository.getOvertimeTop10().stream().map(r -> {
            OvertimeTopResponse d = new OvertimeTopResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setOtHours((BigDecimal) r[2]);
            d.setOtPaid((BigDecimal) r[3]);
            return d;
        }).toList();
    }

    public List<AbsenceWatchlistResponse> getAbsenceWatchlist() {
        return reportRepository.getAbsenceWatchlist().stream().map(r -> {
            AbsenceWatchlistResponse d = new AbsenceWatchlistResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setDepartment((String) r[2]);
            d.setUnpaidDays(((Number) r[3]).longValue());
            d.setLateMinutes(((Number) r[4]).longValue());
            return d;
        }).toList();
    }

    public List<ActiveLoanResponse> getActiveLoans() {
        return reportRepository.getActiveLoans().stream().map(r -> {
            ActiveLoanResponse d = new ActiveLoanResponse();
            d.setLoanId(((Number) r[0]).longValue());
            d.setEmpCode((String) r[1]);
            d.setFullNameAr((String) r[2]);
            d.setLoanType((String) r[3]);
            d.setPrincipalAmount((BigDecimal) r[4]);
            d.setInstallmentsCount(((Number) r[5]).intValue());
            d.setMonthlyInstallment((BigDecimal) r[6]);
            d.setRemainingBalance((BigDecimal) r[7]);
            d.setPaidPct((BigDecimal) r[8]);
            d.setStartPeriod((String) r[9]);
            d.setStatus((String) r[10]);
            return d;
        }).toList();
    }

    public List<ContractExpiringResponse> getContractsExpiring(int months) {
        return reportRepository.getContractsExpiring(months).stream().map(r -> {
            ContractExpiringResponse d = new ContractExpiringResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setContractNo((String) r[2]);
            d.setContractType((String) r[3]);
            d.setStartDate((LocalDate) r[4]);
            d.setEndDate((LocalDate) r[5]);
            d.setDaysLeft(((Number) r[6]).longValue());
            return d;
        }).toList();
    }

    // function to find top attendance
    public List<TopAttendanceResponse> getTopAttendance(String periodCode) {
        return reportRepository.getTopAttendance(periodCode).stream().map(r -> {
            TopAttendanceResponse d = new TopAttendanceResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setPresentDays((java.math.BigDecimal) r[2]);
            return d;
        }).toList();
    }

    // function to find top salaries
    public List<TopNetSalaryResponse> getTopNetSalary(String periodCode) {
        return reportRepository.getTopNetSalary(periodCode).stream().map(r -> {
            TopNetSalaryResponse d = new TopNetSalaryResponse();
            d.setEmpCode((String) r[0]);
            d.setFullNameAr((String) r[1]);
            d.setNetPay((java.math.BigDecimal) r[2]);
            return d;
        }).toList();
    }
}