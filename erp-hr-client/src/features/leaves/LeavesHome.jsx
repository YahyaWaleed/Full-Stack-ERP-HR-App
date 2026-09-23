import { Link } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

function LeavesHome() {
  const { isAdmin } = useAuth();
  return (
    <div>
      <h1>Leave Management</h1>
      <nav className="page-actions">
        <Link className="button-link" to="/dashboard/leaves/list">View All Leave Requests</Link>
        <Link className="button-link" to="/dashboard/leaves/balances">View Leave Balances</Link>
        {isAdmin && <Link className="button-link" to="/dashboard/leaves/create">Create New Leave Request</Link>}
      </nav>
    </div>
  );
}

export default LeavesHome;
