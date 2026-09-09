import { Link } from 'react-router-dom';

function Employee() {
  return (
    <div>
      <h1>Employee Management</h1>
      <nav className="page-actions">
        <Link to="/dashboard/employees/list">View All Employees</Link>
        <br /><br />
        <Link to="/dashboard/employees/create">Create New Employee</Link>
      </nav>
    </div>
  );
}

export default Employee;