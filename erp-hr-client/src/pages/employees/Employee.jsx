import { Link } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';

function Employee() {
  const { isAdmin } = useAuth();

  return (
    <div>
      <h1>Employee Management</h1>
      <nav>
        <Link to="/dashboard/employees/list">View All Employees</Link>
        <br /><br />
        {isAdmin && (
          <Link to="/dashboard/employees/create">Create New Employee</Link>
        )}
      </nav>
    </div>
  );
}

export default Employee;