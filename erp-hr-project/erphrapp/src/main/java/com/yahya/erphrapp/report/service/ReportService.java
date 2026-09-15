package com.yahya.erphrapp.report.service;

import com.yahya.erphrapp.report.dto.*;
import com.yahya.erphrapp.report.repository.ReportRepository;
import jakarta.persistence.Tuple;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    private LocalDate localDate(Tuple t, String col) {
        Object v = t.get(col);
        if (v == null) return null;
        if (v instanceof LocalDate ld) return ld;
        if (v instanceof java.sql.Date d) return d.toLocalDate();
        throw new IllegalStateException("Unexpected date type for column " + col + ": " + v.getClass());
    }

    public List<EmployeeDirectoryResponse> getEmployeeDirectory() {
        return reportRepository.getEmployeeDirectory().stream().map(t -> {
            EmployeeDirectoryResponse d = new EmployeeDirectoryResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setFullNameEn(t.get("full_name_en", String.class));
            d.setGender(String.valueOf(t.get("gender")));
            d.setAge(((Number) t.get("age")).intValue());
            d.setDepartment(t.get("department", String.class));
            d.setJobTitle(t.get("job_title", String.class));
            d.setJobGrade(t.get("job_grade", String.class));
            d.setBranch(t.get("branch", String.class));
            d.setManager(t.get("manager", String.class));
            d.setHireDate(localDate(t, "hire_date"));
            d.setYearsOfService(((Number) t.get("years_of_service")).intValue());
            d.setEmpStatus(t.get("emp_status", String.class));
            d.setContractType(t.get("contract_type", String.class));
            d.setBasicSalary(t.get("basic_salary", BigDecimal.class));
            d.setEmail(t.get("email", String.class));
            d.setMobile(t.get("mobile", String.class));
            return d;
        }).toList();
    }

    public List<HeadcountByDeptResponse> getHeadcountByDept() {
        return reportRepository.getHeadcountByDept().stream().map(t -> {
            HeadcountByDeptResponse d = new HeadcountByDeptResponse();
            d.setCode(t.get("code", String.class));
            d.setDepartment(t.get("department", String.class));
            d.setBranch(t.get("branch", String.class));
            d.setHeadcount(((Number) t.get("headcount")).longValue());
            d.setMales(((Number) t.get("males")).longValue());
            d.setFemales(((Number) t.get("females")).longValue());
            d.setAvgServiceYears(t.get("avg_service_years", BigDecimal.class));
            d.setAvgBasicSalary(t.get("avg_basic_salary", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<PayrollRegisterResponse> getPayrollRegister(String periodCode) {
        return reportRepository.getPayrollRegister(periodCode).stream().map(t -> {
            PayrollRegisterResponse d = new PayrollRegisterResponse();
            d.setPayslipId(((Number) t.get("payslip_id")).longValue());
            d.setPayslipNo(t.get("payslip_no", String.class));
            d.setPeriodCode(t.get("period_code", String.class));
            d.setPayDate(localDate(t, "pay_date"));
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setDepartment(t.get("department", String.class));
            d.setJobTitle(t.get("job_title", String.class));
            d.setBasicSalary(t.get("basic_salary", BigDecimal.class));
            d.setGrossPay(t.get("gross_pay", BigDecimal.class));
            d.setTotalDeductions(t.get("total_deductions", BigDecimal.class));
            d.setInsuranceEmployee(t.get("insurance_employee", BigDecimal.class));
            d.setIncomeTax(t.get("income_tax", BigDecimal.class));
            d.setNetPay(t.get("net_pay", BigDecimal.class));
            d.setWorkedDays(t.get("worked_days", BigDecimal.class));
            d.setAbsentDays(t.get("absent_days", BigDecimal.class));
            d.setOvertimeHours(t.get("overtime_hours", BigDecimal.class));
            d.setInsuranceEmployer(t.get("insurance_employer", BigDecimal.class));
            d.setTotalCompanyCost(t.get("total_company_cost", BigDecimal.class));
            d.setStatus(t.get("status", String.class));
            d.setCurrency(t.get("currency", String.class));
            return d;
        }).toList();
    }

    public List<PayrollCostByDeptResponse> getPayrollCostByDept(String periodCode) {
        return reportRepository.getPayrollCostByDept(periodCode).stream().map(t -> {
            PayrollCostByDeptResponse d = new PayrollCostByDeptResponse();
            d.setPeriodCode(t.get("period_code", String.class));
            d.setDepartment(t.get("department", String.class));
            d.setEmployees(((Number) t.get("employees")).longValue());
            d.setTotalBasic(t.get("total_basic", BigDecimal.class));
            d.setTotalGross(t.get("total_gross", BigDecimal.class));
            d.setTotalTax(t.get("total_tax", BigDecimal.class));
            d.setInsuranceEmployee(t.get("insurance_employee", BigDecimal.class));
            d.setInsuranceEmployer(t.get("insurance_employer", BigDecimal.class));
            d.setTotalNet(t.get("total_net", BigDecimal.class));
            d.setCompanyCost(t.get("company_cost", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<PayrollTrendResponse> getPayrollTrend() {
        return reportRepository.getPayrollTrend().stream().map(t -> {
            PayrollTrendResponse d = new PayrollTrendResponse();
            d.setPeriodCode(t.get("period_code", String.class));
            d.setEmployees(((Number) t.get("employees")).longValue());
            d.setGross(t.get("gross", BigDecimal.class));
            d.setDeductions(t.get("deductions", BigDecimal.class));
            d.setNet(t.get("net", BigDecimal.class));
            d.setCompanyCost(t.get("company_cost", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<TaxInsuranceLiabilityResponse> getTaxInsuranceLiability() {
        return reportRepository.getTaxInsuranceLiability().stream().map(t -> {
            TaxInsuranceLiabilityResponse d = new TaxInsuranceLiabilityResponse();
            d.setPeriodCode(t.get("period_code", String.class));
            d.setIncomeTaxDue(t.get("income_tax_due", BigDecimal.class));
            d.setInsuranceEmployeeShare(t.get("insurance_employee_share", BigDecimal.class));
            d.setInsuranceEmployerShare(t.get("insurance_employer_share", BigDecimal.class));
            d.setTotalInsuranceDue(t.get("total_insurance_due", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<BankTransferResponse> getBankTransfer(String periodCode) {
        return reportRepository.getBankTransfer(periodCode).stream().map(t -> {
            BankTransferResponse d = new BankTransferResponse();
            d.setFullNameEn(t.get("full_name_en", String.class));
            d.setBankName(t.get("bank_name", String.class));
            d.setBankAccount(t.get("bank_account", String.class));
            d.setAmount(t.get("amount", BigDecimal.class));
            d.setReference(t.get("reference", String.class));
            d.setPaidOn(localDate(t, "paid_on"));
            return d;
        }).toList();
    }

    public List<LeaveBalanceReportResponse> getLeaveBalances(int fiscalYear) {
        return reportRepository.getLeaveBalances(fiscalYear).stream().map(t -> {
            LeaveBalanceReportResponse d = new LeaveBalanceReportResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setDepartment(t.get("department", String.class));
            d.setLeaveType(t.get("leave_type", String.class));
            d.setFiscalYear(((Number) t.get("fiscal_year")).intValue());
            d.setEntitledDays(t.get("entitled_days", BigDecimal.class));
            d.setCarriedForward(t.get("carried_forward", BigDecimal.class));
            d.setUsedDays(t.get("used_days", BigDecimal.class));
            d.setRemainingDays(t.get("remaining_days", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<LeaveRequestLogResponse> getLeaveRequestLog(String status) {
        return reportRepository.getLeaveRequestLog(status).stream().map(t -> {
            LeaveRequestLogResponse d = new LeaveRequestLogResponse();
            d.setRequestId(((Number) t.get("request_id")).longValue());
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setLeaveType(t.get("leave_type", String.class));
            d.setStartDate(localDate(t, "start_date"));
            d.setEndDate(localDate(t, "end_date"));
            d.setDaysCount(t.get("days_count", BigDecimal.class));
            d.setStatus(t.get("status", String.class));
            d.setApprovedBy(t.get("approved_by", String.class));
            d.setAppliedOn(localDate(t, "applied_on"));
            d.setDecidedOn(localDate(t, "decided_on"));
            d.setReason(t.get("reason", String.class));
            return d;
        }).toList();
    }

    public List<OvertimeTopResponse> getOvertimeTop10() {
        return reportRepository.getOvertimeTop10().stream().map(t -> {
            OvertimeTopResponse d = new OvertimeTopResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setOtHours(t.get("ot_hours", BigDecimal.class));
            d.setOtPaid(t.get("ot_paid", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<AbsenceWatchlistResponse> getAbsenceWatchlist() {
        return reportRepository.getAbsenceWatchlist().stream().map(t -> {
            AbsenceWatchlistResponse d = new AbsenceWatchlistResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setDepartment(t.get("department", String.class));
            d.setUnpaidDays(((Number) t.get("unpaid_days")).longValue());
            d.setLateMinutes(((Number) t.get("late_minutes")).longValue());
            return d;
        }).toList();
    }

    public List<ActiveLoanResponse> getActiveLoans() {
        return reportRepository.getActiveLoans().stream().map(t -> {
            ActiveLoanResponse d = new ActiveLoanResponse();
            d.setLoanId(((Number) t.get("loan_id")).longValue());
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setLoanType(t.get("loan_type", String.class));
            d.setPrincipalAmount(t.get("principal_amount", BigDecimal.class));
            d.setInstallmentsCount(((Number) t.get("installments_count")).intValue());
            d.setMonthlyInstallment(t.get("monthly_installment", BigDecimal.class));
            d.setRemainingBalance(t.get("remaining_balance", BigDecimal.class));
            d.setPaidPct(t.get("paid_pct", BigDecimal.class));
            d.setStartPeriod(t.get("start_period", String.class));
            d.setStatus(t.get("status", String.class));
            return d;
        }).toList();
    }

    public List<ContractExpiringResponse> getContractsExpiring(int months) {
        return reportRepository.getContractsExpiring(months).stream().map(t -> {
            ContractExpiringResponse d = new ContractExpiringResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setContractNo(t.get("contract_no", String.class));
            d.setContractType(t.get("contract_type", String.class));
            d.setStartDate(localDate(t, "start_date"));
            d.setEndDate(localDate(t, "end_date"));
            d.setDaysLeft(((Number) t.get("days_left")).longValue());
            return d;
        }).toList();
    }

    public List<TopAttendanceResponse> getTopAttendance(String periodCode) {
        return reportRepository.getTopAttendance(periodCode).stream().map(t -> {
            TopAttendanceResponse d = new TopAttendanceResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setPresentDays(t.get("present_days", BigDecimal.class));
            return d;
        }).toList();
    }

    public List<TopNetSalaryResponse> getTopNetSalary(String periodCode) {
        return reportRepository.getTopNetSalary(periodCode).stream().map(t -> {
            TopNetSalaryResponse d = new TopNetSalaryResponse();
            d.setEmpCode(t.get("emp_code", String.class));
            d.setFullNameAr(t.get("full_name_ar", String.class));
            d.setNetPay(t.get("net_pay", BigDecimal.class));
            return d;
        }).toList();
    }
}