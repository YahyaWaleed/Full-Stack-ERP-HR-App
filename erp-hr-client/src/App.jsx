import { lazy, Suspense } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import ErrorBoundary from './shared/components/ErrorBoundary';
import { AuthProvider } from './features/auth/AuthContext';
import ProtectedRoute from './features/auth/ProtectedRoute';
import LoginPage from './features/auth/LoginPage';
import DashboardLayout from './features/dashboard/DashboardLayout';
import NotFound from './features/dashboard/NotFound';

// every page is its own chunk, downloaded the first time it's opened (review 8.1)
const HomeOverview = lazy(() => import('./features/dashboard/HomeOverview'));
const EmployeesHome = lazy(() => import('./features/employees/EmployeesHome'));
const EmployeeList = lazy(() => import('./features/employees/EmployeeList'));
const EmployeeCreate = lazy(() => import('./features/employees/EmployeeCreate'));
const EmployeeEdit = lazy(() => import('./features/employees/EmployeeEdit'));
const EmployeeDetails = lazy(() => import('./features/employees/EmployeeDetails'));
const EmployeeContractRenew = lazy(() => import('./features/employees/EmployeeContractRenew'));
const AttendanceList = lazy(() => import('./features/attendance/AttendanceList'));
const LeavesHome = lazy(() => import('./features/leaves/LeavesHome'));
const LeaveRequestsList = lazy(() => import('./features/leaves/LeaveRequestsList'));
const LeaveRequestCreate = lazy(() => import('./features/leaves/LeaveRequestCreate'));
const LeaveRequestDetails = lazy(() => import('./features/leaves/LeaveRequestDetails'));
const LeaveBalanceList = lazy(() => import('./features/leaves/LeaveBalanceList'));
const LoansHome = lazy(() => import('./features/loans/LoansHome'));
const LoanList = lazy(() => import('./features/loans/LoanList'));
const LoanCreate = lazy(() => import('./features/loans/LoanCreate'));
const LoanDetails = lazy(() => import('./features/loans/LoanDetails'));
const PayrollHome = lazy(() => import('./features/payroll/PayrollHome'));
const PayrollPeriodList = lazy(() => import('./features/payroll/PayrollPeriodList'));
const PayrollPeriodCreate = lazy(() => import('./features/payroll/PayrollPeriodCreate'));
const PayrollPeriodDetails = lazy(() => import('./features/payroll/PayrollPeriodDetails'));
const PayslipDetails = lazy(() => import('./features/payroll/PayslipDetails'));
const BranchList = lazy(() => import('./features/organization/BranchList'));
const DepartmentList = lazy(() => import('./features/organization/DepartmentList'));
const JobTitleList = lazy(() => import('./features/organization/JobTitleList'));
const OrgUnitEmployees = lazy(() => import('./features/organization/OrgUnitEmployees'));
const ReportsIndex = lazy(() => import('./features/reports/ReportsIndex'));
const ReportPage = lazy(() => import('./features/reports/ReportPage'));
const AuditLog = lazy(() => import('./features/audit/AuditLog'));

// mirrors @PreAuthorize("hasRole('HR_ADMIN')") on the backend
const admin = (element) => <ProtectedRoute adminOnly>{element}</ProtectedRoute>;

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ErrorBoundary>
          <Suspense fallback={<p className="muted page-loading">Loading…</p>}>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />

              <Route path="/dashboard" element={<ProtectedRoute><DashboardLayout /></ProtectedRoute>}>
                <Route index element={<HomeOverview />} />

                <Route path="employees" element={<EmployeesHome />} />
                <Route path="employees/list" element={<EmployeeList />} />
                <Route path="employees/create" element={admin(<EmployeeCreate />)} />
                <Route path="employees/:id" element={<EmployeeDetails />} />
                <Route path="employees/:id/edit" element={admin(<EmployeeEdit />)} />
                <Route path="employees/:id/contracts/renew" element={admin(<EmployeeContractRenew />)} />
                <Route path="attendance" element={<AttendanceList />} />

                <Route path="leaves" element={<LeavesHome />} />
                <Route path="leaves/list" element={<LeaveRequestsList />} />
                <Route path="leaves/create" element={admin(<LeaveRequestCreate />)} />
                <Route path="leaves/balances" element={<LeaveBalanceList />} />
                <Route path="leaves/:id" element={<LeaveRequestDetails />} />

                <Route path="loans" element={<LoansHome />} />
                <Route path="loans/list" element={<LoanList />} />
                <Route path="loans/create" element={admin(<LoanCreate />)} />
                <Route path="loans/:id" element={<LoanDetails />} />

                <Route path="payroll" element={<PayrollHome />} />
                <Route path="payroll/periods" element={<PayrollPeriodList />} />
                <Route path="payroll/periods/create" element={admin(<PayrollPeriodCreate />)} />
                <Route path="payroll/periods/:periodCode" element={<PayrollPeriodDetails />} />
                <Route path="payroll/payslips/:id" element={admin(<PayslipDetails />)} />

                <Route path="branches" element={<BranchList />} />
                <Route path="branches/:branchId/employees" element={<OrgUnitEmployees />} />
                <Route path="departments" element={<DepartmentList />} />
                <Route path="departments/:deptId/employees" element={<OrgUnitEmployees />} />
                <Route path="jobs" element={<JobTitleList />} />
                <Route path="jobs/:jobTitleId/employees" element={<OrgUnitEmployees />} />

                <Route path="reports" element={admin(<ReportsIndex />)} />
                <Route path="reports/:slug" element={admin(<ReportPage />)} />
                <Route path="audit" element={admin(<AuditLog />)} />

                <Route path="*" element={<NotFound />} />
              </Route>

              <Route path="*" element={<NotFound />} />
            </Routes>
          </Suspense>
        </ErrorBoundary>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
