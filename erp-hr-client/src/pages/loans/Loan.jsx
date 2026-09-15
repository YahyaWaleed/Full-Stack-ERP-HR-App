import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';

function Loan() {
  const { isAdmin } = useAuth();

  return (
    <div>
      <h1>Loan Management</h1>
      <nav>
        <Link to="/dashboard/loans/list">View All Loans</Link><br /><br />
        {isAdmin && (
          <Link to="/dashboard/loans/create">Create New Loan</Link>
        )}
      </nav>
    </div>
  );
}

export default Loan;