import { Link } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

function PayrollHome() {
  const { isAdmin } = useAuth();
  return (
    <div>
      <h1>Payroll</h1>
      <nav className="page-actions">
        <Link className="button-link" to="/dashboard/payroll/periods">View Payroll Periods</Link>
        {isAdmin && <Link className="button-link" to="/dashboard/payroll/periods/create">Open New Period</Link>}
      </nav>
    </div>
  );
}

export default PayrollHome;
