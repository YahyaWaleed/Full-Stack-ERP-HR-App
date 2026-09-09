import { Link } from 'react-router-dom';

function Payroll() {
  return (
    <div>
      <h1>Payroll Management</h1>
      <nav className="page-actions">
        <Link to="/dashboard/payroll/periods">View All Periods</Link><br /><br />
        <Link to="/dashboard/payroll/periods/create">Open New Period</Link>
      </nav>
    </div>
  );
}

export default Payroll;