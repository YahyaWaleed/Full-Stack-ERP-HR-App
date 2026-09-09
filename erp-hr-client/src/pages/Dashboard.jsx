import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import LiveClock from '../components/LiveClock';
import ThemeToggle from '../components/ThemeToggle';  

function Dashboard() {
  const location = useLocation();
  const navigate = useNavigate();

  // Retrieve username from location state or local storage
  const username = location.state?.username || localStorage.getItem('username') || 'HR Manager';

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    navigate('/login');
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <LiveClock />
      <ThemeToggle />
      {/* Left Navigation Sidebar */}
      <div className="sidebar" style={{ width: '220px', borderRight: '1px solid #ccc', padding: '15px' }}>
        <h2>ERP HR App</h2>
        <p>Welcome, <strong>{username}</strong></p>
        <button onClick={handleLogout}>Logout</button>
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

          <p><strong>Reports</strong></p>
          <Link to="/dashboard/reports">Reports</Link><br /><br />
        </nav>
      </div>

      {/* Main Content Pane where child routes mount */}
      <div style={{ flex: 1, padding: '20px' }}>
        <Outlet context={{ username }} />
      </div>
    </div>
  );
}

export default Dashboard;