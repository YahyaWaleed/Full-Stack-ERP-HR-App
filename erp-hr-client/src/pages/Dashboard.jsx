import { Outlet } from 'react-router-dom';
import LiveClock from '../components/LiveClock';
import ThemeToggle from '../components/ThemeToggle';
import { useAuth } from '../auth/AuthContext';
import { Link } from 'react-router-dom';

function Dashboard() {
  const { username, isAdmin, logout } = useAuth();

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <LiveClock />
      <ThemeToggle />

      <div className="sidebar">
        <h2>ERP HR App</h2>
        <p>Welcome, <strong>{username}</strong></p>
        <button onClick={logout}>Logout</button>
        <hr />

        <nav>
          <p><strong>Main Menu</strong></p>
          <Link to="/dashboard">Dashboard Overview</Link><br /><br />

          <p><strong>HR Management</strong></p>
          <Link to="/dashboard/employees">Employees</Link><br /><br />
          <Link to="/dashboard/attendance">Attendance</Link><br /><br />
          <Link to="/dashboard/leaves">Leaves</Link><br /><br />
          <Link to="/dashboard/loans">Loans</Link><br /><br />
          <Link to="/dashboard/payroll">Payroll</Link><br /><br />

          <p><strong>Organization</strong></p>
          <Link to="/dashboard/branches">Branches</Link><br /><br />
          <Link to="/dashboard/departments">Departments</Link><br /><br />
          <Link to="/dashboard/jobs">Job Titles</Link><br /><br />

          {/* reports expose salaries and bank data -- HR_ADMIN only on the backend */}
          {isAdmin && (
            <>
              <p><strong>Reports</strong></p>
              <Link to="/dashboard/reports">Reports</Link><br /><br />
            </>
          )}
        </nav>
      </div>

      <div className="dashboard-content">
        <Outlet context={{ username }} />
      </div>
    </div>
  );
}

export default Dashboard;