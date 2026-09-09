import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './auth/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import Dashboard from './pages/Dashboard';
import HomeOverview from './pages/HomeOverview';

// Core Modules
import Employee from './pages/employees/Employee';
import EmployeeList from './pages/employees/EmployeeList';
import EmployeeCreate from './pages/employees/EmployeeCreate';
import EmployeeDetails from './pages/employees/EmployeeDetails';
import EmployeeContractRenew from './pages/employees/EmployeeContractRenew';
import AttendanceList from './pages/attendance/AttendanceList';
import Leave from './pages/leaves/Leave';
import LeaveRequestsList from './pages/leaves/LeaveRequestsList';
import LeaveRequestCreate from './pages/leaves/LeaveRequestCreate';
import LeaveBalanceList from './pages/leaves/LeaveBalanceList';
import LeaveRequestDetails from './pages/leaves/LeaveRequestDetails';
import Loan from './pages/loans/Loan';
import LoanList from './pages/loans/LoanList';
import LoanCreate from './pages/loans/LoanCreate';
import LoanDetails from './pages/loans/LoanDetails';
import Payroll from './pages/payroll/Payroll';
import PayrollPeriodList from './pages/payroll/PayrollPeriodList';
import PayrollPeriodCreate from './pages/payroll/PayrollPeriodCreate';
import PayrollPeriodDetails from './pages/payroll/PayrollPeriodDetails';
import PayslipDetails from './pages/payroll/PayslipDetails';

// Organization Modules
import BranchList from './pages/organization/BranchList';
import DepartmentList from './pages/organization/DepartmentList';
import JobTitleList from './pages/organization/JobTitleList';
import BranchEmployees from './pages/organization/BranchEmployees';
import DepartmentEmployees from './pages/organization/DepartmentEmployees';
import JobTitleEmployees from './pages/organization/JobTitleEmployees';

// Reports
import Report from './pages/reports/Report';
import EmployeeDirectoryReport from './pages/reports/EmployeeDirectoryReport';
import HeadCountByDeptReport from './pages/reports/HeadCountByDeptReport';
import PayrollRegisterReport from './pages/reports/PayrollRegisterReport';
import PayrollCostByDeptReport from './pages/reports/PayrollCostByDeptReport';
import PayrollTrendReport from './pages/reports/PayrollTrendReport';
import TaxInsuranceLiabilityReport from './pages/reports/TaxInsuranceLiabilityReport';
import BankTransferReport from './pages/reports/BankTransferReport';
import LeaveBalancesReport from './pages/reports/LeaveBalancesReport';
import LeaveRequestLogReport from './pages/reports/LeaveRequestLogReport';
import OvertimeTopf10Report from './pages/reports/OvertimeTopf10Report';
import AbsenceWatchlistReport from './pages/reports/AbsenceWatchlistReport';
import ActiveLoansReport from './pages/reports/ActiveLoansReport';
import ContractsExpiringReport from './pages/reports/ContractsExpiringReport';
import TopAttendanceReport from './pages/reports/TopAttendanceReport';
import TopNetSalaryReport from './pages/reports/TopNetSalaryReport';


function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        {/* Protected Dashboard Shell Route */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        >
          {/* Default Child Page (Dashboard Overview) */}
          <Route index element={<HomeOverview />} />

          {/* HR Management Sub-routes */}
          <Route path="employees" element={<Employee />} />
          <Route path="employees/list" element={<EmployeeList />} />
          <Route path="employees/create" element={<EmployeeCreate />} />
          <Route path="employees/:id" element={<EmployeeDetails />} />
          <Route path="employees/:id/contracts/renew" element={<EmployeeContractRenew />} />
          <Route path="attendance" element={<AttendanceList />} />
          <Route path="leaves" element={<Leave />} />
          <Route path="leaves/list" element={<LeaveRequestsList />} />
          <Route path="leaves/create" element={<LeaveRequestCreate />} />
          <Route path="leaves/balances" element={<LeaveBalanceList />} />
          <Route path="leaves/:id" element={<LeaveRequestDetails />} />
          <Route path="loans" element={<Loan />} />
          <Route path="loans/list" element={<LoanList />} />
          <Route path="loans/create" element={<LoanCreate />} />
          <Route path="loans/:id" element={<LoanDetails />} />
          <Route path="payroll" element={<Payroll />} />
          <Route path="payroll/periods" element={<PayrollPeriodList />} />
          <Route path="payroll/periods/create" element={<PayrollPeriodCreate />} />
          <Route path="payroll/periods/:periodCode" element={<PayrollPeriodDetails />} />
          <Route path="payroll/payslips/:id" element={<PayslipDetails />} />

          {/* Organization Sub-routes */}
          <Route path="branches" element={<BranchList />} />
          <Route path="departments" element={<DepartmentList />} />
          <Route path="branches/:branchId/employees" element={<BranchEmployees />} />
          <Route path="departments/:deptId/employees" element={<DepartmentEmployees />} />
          <Route path="jobs" element={<JobTitleList />} />
          <Route path="jobs/:jobTitleId/employees" element={<JobTitleEmployees />} />

          {/* Reports */}
          <Route path="reports" element={<Report />} />
          <Route path="reports/employee-directory" element={<EmployeeDirectoryReport />} />
          <Route path="reports/headcount-by-department" element={<HeadCountByDeptReport />} />
          <Route path="reports/payroll-register" element={<PayrollRegisterReport />} />
          <Route path="reports/payroll-cost-by-department" element={<PayrollCostByDeptReport />} />
          <Route path="reports/payroll-trend" element={<PayrollTrendReport />} />
          <Route path="reports/tax-insurance-liability" element={<TaxInsuranceLiabilityReport />} />
          <Route path="reports/bank-transfer" element={<BankTransferReport />} />
          <Route path="reports/leave-balances" element={<LeaveBalancesReport />} />
          <Route path="reports/leave-requests" element={<LeaveRequestLogReport />} />
          <Route path="reports/overtime-top10" element={<OvertimeTopf10Report />} />
          <Route path="reports/absence-watchlist" element={<AbsenceWatchlistReport />} />
          <Route path="reports/active-loans" element={<ActiveLoansReport />} />
          <Route path="reports/contracts-expiring" element={<ContractsExpiringReport />} />
          <Route path="reports/top-attendance" element={<TopAttendanceReport />} />
          <Route path="reports/top-net-salary" element={<TopNetSalaryReport />} />
        </Route>

        {/* Fallback Catch-all */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;