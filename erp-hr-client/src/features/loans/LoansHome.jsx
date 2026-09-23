import { Link } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

function LoansHome() {
  const { isAdmin } = useAuth();
  return (
    <div>
      <h1>Loan Management</h1>
      <nav className="page-actions">
        <Link className="button-link" to="/dashboard/loans/list">View All Loans</Link>
        {isAdmin && <Link className="button-link" to="/dashboard/loans/create">Create New Loan</Link>}
      </nav>
    </div>
  );
}

export default LoansHome;
