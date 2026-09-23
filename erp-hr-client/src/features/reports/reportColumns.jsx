import { Link } from 'react-router-dom';
import { statusClass } from '../../shared/utils/statusClass';

// Which columns each report shows. The report list, titles and parameters come from the backend
// (GET /reports); adding a report is one entry in ReportRegistry.java plus, optionally, its columns here.
// Reports without an entry show every field the server returns.
const rank = { key: 'rank', label: 'Rank', render: (_, i) => i + 1 };
const status = { key: 'status', label: 'Status', render: (r) => <span className={statusClass(r.status)}>{r.status}</span> };
const money = (key, label) => ({ key, label, align: 'right' });

export const REPORT_COLUMNS = {
  'employee-directory': [
    { key: 'empCode', label: 'Code' }, { key: 'fullNameEn', label: 'Name' }, { key: 'department', label: 'Dept' },
    { key: 'jobTitle', label: 'Job Title' }, { key: 'branch', label: 'Branch' }, { key: 'empStatus', label: 'Status' },
    { key: 'hireDate', label: 'Hire Date' }, money('basicSalary', 'Salary'), { key: 'email', label: 'Email' },
  ],
  'headcount-by-department': [
    { key: 'department', label: 'Department' }, { key: 'branch', label: 'Branch' }, money('headcount', 'Headcount'),
    money('males', 'Male'), money('females', 'Female'), money('avgServiceYears', 'Avg Years'), money('avgBasicSalary', 'Avg Salary'),
  ],
  'payroll-register': [
    { key: 'empCode', label: 'Emp Code' }, { key: 'fullNameAr', label: 'Name' }, { key: 'department', label: 'Department' },
    money('basicSalary', 'Basic'), money('grossPay', 'Gross'), money('totalDeductions', 'Deductions'), money('netPay', 'Net'),
    status,
    { key: 'view', label: '', render: (r) => <Link to={`/dashboard/payroll/payslips/${r.payslipId}`}>Payslip</Link> },
  ],
  'payroll-cost-by-department': [
    { key: 'department', label: 'Department' }, money('employees', 'Employees'), money('totalGross', 'Total Gross'),
    money('totalNet', 'Total Net'), money('companyCost', 'Company Cost'),
  ],
  'payroll-trend': [
    { key: 'periodCode', label: 'Period' }, money('employees', 'Employees'), money('gross', 'Gross'),
    money('deductions', 'Deductions'), money('net', 'Net Pay'), money('companyCost', 'Company Cost'),
  ],
  'tax-insurance-liability': [
    { key: 'periodCode', label: 'Period' }, money('incomeTaxDue', 'Income Tax Due'), money('insuranceEmployeeShare', 'Insurance (Employee)'),
    money('insuranceEmployerShare', 'Insurance (Employer)'), money('totalInsuranceDue', 'Total Insurance'),
  ],
  'bank-transfer': [
    { key: 'fullNameEn', label: 'Employee' }, { key: 'bankName', label: 'Bank' }, { key: 'bankAccount', label: 'Account' },
    money('amount', 'Amount'), { key: 'reference', label: 'Reference' }, { key: 'paidOn', label: 'Paid On' },
  ],
  'leave-balances': [
    { key: 'employee', label: 'Employee', render: (r) => `${r.empCode} - ${r.fullNameAr}` }, { key: 'leaveType', label: 'Leave Type' },
    money('entitledDays', 'Entitled'), money('usedDays', 'Used'), money('remainingDays', 'Remaining'),
  ],
  'leave-requests': [
    { key: 'fullNameAr', label: 'Employee' }, { key: 'leaveType', label: 'Type' }, { key: 'startDate', label: 'Start' },
    { key: 'endDate', label: 'End' }, money('daysCount', 'Days'), status, { key: 'reason', label: 'Reason' },
    { key: 'manage', label: '', render: (r) => r.status === 'PENDING' && <Link to={`/dashboard/leaves/${r.requestId}`}>Manage</Link> },
  ],
  'overtime-top10': [rank, { key: 'fullNameAr', label: 'Employee' }, money('otHours', 'Overtime Hours'), money('otPaid', 'Overtime Paid')],
  'absence-watchlist': [
    { key: 'fullNameAr', label: 'Employee' }, { key: 'department', label: 'Department' },
    money('unpaidDays', 'Unpaid Days'), money('lateMinutes', 'Late Minutes'),
  ],
  'active-loans': [
    { key: 'fullNameAr', label: 'Employee' }, { key: 'loanType', label: 'Type' }, money('principalAmount', 'Principal'),
    money('remainingBalance', 'Remaining'), money('paidPct', 'Paid %'),
    { key: 'view', label: '', render: (r) => <Link to={`/dashboard/loans/${r.loanId}`}>View</Link> },
  ],
  'contracts-expiring': [
    { key: 'fullNameAr', label: 'Employee' }, { key: 'contractNo', label: 'Contract No' }, { key: 'contractType', label: 'Type' },
    { key: 'endDate', label: 'End Date' }, money('daysLeft', 'Days Left'),
    { key: 'renew', label: '', render: (r) => <Link to={`/dashboard/employees/${r.empId}/contracts/renew`}>Renew Contract</Link> },
  ],
  'top-attendance': [rank, { key: 'fullNameAr', label: 'Employee' }, money('presentDays', 'Present Days')],
  'top-net-salary': [rank, { key: 'fullNameAr', label: 'Employee' }, money('netPay', 'Net Pay')],
};
