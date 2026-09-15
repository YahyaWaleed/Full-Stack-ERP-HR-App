package com.yahya.erphrapp.report.repository;

import jakarta.persistence.Tuple;

import java.util.List;

public interface ReportRepository {

    List<Tuple> getEmployeeDirectory();
    List<Tuple> getHeadcountByDept();
    List<Tuple> getPayrollRegister(String periodCode);
    List<Tuple> getPayrollCostByDept(String periodCode);
    List<Tuple> getPayrollTrend();
    List<Tuple> getTaxInsuranceLiability();
    List<Tuple> getBankTransfer(String periodCode);
    List<Tuple> getLeaveBalances(int fiscalYear);
    List<Tuple> getLeaveRequestLog(String status);
    List<Tuple> getOvertimeTop10();
    List<Tuple> getAbsenceWatchlist();
    List<Tuple> getActiveLoans();
    List<Tuple> getContractsExpiring(int months);
    List<Tuple> getTopAttendance(String periodCode);
    List<Tuple> getTopNetSalary(String periodCode);
}