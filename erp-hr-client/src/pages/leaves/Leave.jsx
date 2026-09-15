import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';

function Leave() {
  const { isAdmin } = useAuth();

  return (
    <div>
      <h1>Leave Management</h1>
      <nav>
        <Link to="/dashboard/leaves/list">View All Leave Requests</Link><br /><br />
        <Link to="/dashboard/leaves/balances">View Leave Balances</Link><br /><br />
        {isAdmin && (
          <Link to="/dashboard/leaves/create">Create New Leave Request</Link>
        )}
      </nav>
    </div>
  );
}

export default Leave;