import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';

function Payroll() {
  const { isAdmin } = useAuth();

  return (
    <div>
      <h1>Payroll Management</h1>
      <nav>
        <Link to="/dashboard/payroll/periods">View All Periods</Link><br /><br />
        {isAdmin && (
          <Link to="/dashboard/payroll/periods/create">Open New Period</Link>
        )}
      </nav>
    </div>
  );
}

export default Payroll;