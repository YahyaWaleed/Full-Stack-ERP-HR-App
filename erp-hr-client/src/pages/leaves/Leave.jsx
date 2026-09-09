import { Link } from 'react-router-dom';

function Leave() {
  return (
    <div>
      <h1>Leave Management</h1>
      <nav className="page-actions">
        <Link to="/dashboard/leaves/create">Create Leave Request</Link><br /><br />
        <Link to="/dashboard/leaves/list">View All Leave Requests</Link><br /><br />
        <Link to="/dashboard/leaves/balances">View Leave Balances</Link>
      </nav>
    </div>
  );
}

export default Leave;