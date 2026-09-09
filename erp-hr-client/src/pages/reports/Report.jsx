import { Link } from 'react-router-dom';

function Report() {
  return (
    <div>
      <h1>Reports</h1>
      <nav className="page-actions">
        <Link to="/dashboard/reports/employee-directory">Employee Directory</Link><br /><br />
        <Link to="/dashboard/reports/headcount-by-department">Headcount by Department</Link><br /><br />
        <Link to="/dashboard/reports/payroll-register">Payroll Register</Link><br /><br />
        <Link to="/dashboard/reports/payroll-cost-by-department">Payroll Cost by Department</Link><br /><br />
        <Link to="/dashboard/reports/payroll-trend">Payroll Trend</Link><br /><br />
        <Link to="/dashboard/reports/tax-insurance-liability">Tax & Insurance Liability</Link><br /><br />
        <Link to="/dashboard/reports/bank-transfer">Bank Transfer File</Link><br /><br />
        <Link to="/dashboard/reports/leave-balances">Leave Balances</Link><br /><br />
        <Link to="/dashboard/reports/leave-requests">Leave Request Log</Link><br /><br />
        <Link to="/dashboard/reports/overtime-top10">Top Overtime Earners</Link><br /><br />
        <Link to="/dashboard/reports/absence-watchlist">Absence Watch-list</Link><br /><br />
        <Link to="/dashboard/reports/active-loans">Active Loans</Link><br /><br />
        <Link to="/dashboard/reports/contracts-expiring">Contracts Expiring</Link>
        <Link to="/dashboard/reports/top-attendance">Top 10 by Attendance</Link><br /><br />
        <Link to="/dashboard/reports/top-net-salary">Top 10 by Net Salary</Link>
      </nav>
    </div>
  );
}

export default Report;