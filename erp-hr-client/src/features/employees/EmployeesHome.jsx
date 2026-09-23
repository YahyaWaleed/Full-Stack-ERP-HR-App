import { Link } from 'react-router-dom';
import { useAuth } from '../auth/useAuth';

function EmployeesHome() {
  const { isAdmin } = useAuth();
  return (
    <div>
      <h1>Employee Management</h1>
      <nav className="page-actions">
        <Link className="button-link" to="/dashboard/employees/list">View All Employees</Link>
        {isAdmin && <Link className="button-link" to="/dashboard/employees/create">Create New Employee</Link>}
      </nav>
    </div>
  );
}

export default EmployeesHome;
