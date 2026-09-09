package com.yahya.erphrapp.report.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository {

    List<Object[]> getEmployeeDirectory();

    List<Object[]> getHeadcountByDept();

    List<Object[]> getPayrollRegister(String periodCode);

    List<Object[]> getPayrollCostByDept(String periodCode);

    List<Object[]> getPayrollTrend();

    List<Object[]> getTaxInsuranceLiability();

    List<Object[]> getBankTransfer(String periodCode);

    List<Object[]> getLeaveBalances(int fiscalYear);

    List<Object[]> getLeaveRequestLog(String status);

    List<Object[]> getOvertimeTop10();

    List<Object[]> getAbsenceWatchlist();

    List<Object[]> getActiveLoans();

    List<Object[]> getContractsExpiring(int months);

    // to find top 10 attendance by number of present days in a single period
    @Query(value = "SELECT e.emp_code, e.full_name_ar, a.present_days " +
            "FROM attendance_summary a " +
            "JOIN employees e ON e.emp_id = a.emp_id " +
            "JOIN payroll_periods pp ON pp.period_id = a.period_id " +
            "WHERE pp.period_code = :periodCode " +
            "ORDER BY a.present_days DESC LIMIT 10", nativeQuery = true)
    List<Object[]> getTopAttendance(@Param("periodCode") String periodCode);

    // to get top 10 employees with highest salaries in a period
    @Query(value = "SELECT e.emp_code, e.full_name_ar, p.net_pay " +
            "FROM payslips p " +
            "JOIN employees e ON e.emp_id = p.emp_id " +
            "JOIN payroll_periods pp ON pp.period_id = p.period_id " +
            "WHERE pp.period_code = :periodCode " +
            "ORDER BY p.net_pay DESC LIMIT 10", nativeQuery = true)
    List<Object[]> getTopNetSalary(@Param("periodCode") String periodCode);
}