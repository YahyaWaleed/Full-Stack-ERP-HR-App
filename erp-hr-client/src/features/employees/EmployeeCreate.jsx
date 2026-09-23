import { useNavigate } from 'react-router-dom';
import EmployeeForm from './EmployeeForm';
import { employeesApi } from './api';

function EmployeeCreate() {
  const navigate = useNavigate();

  const create = async (form) => {
    const created = await employeesApi.create(form);
    navigate(`/dashboard/employees/${created.id}`);
  };

  return (
    <div>
      <h1>Create Employee</h1>
      <p className="muted">The employee code, first contract number and leave balances are created automatically.</p>
      <EmployeeForm onSubmit={create} submitLabel="Create Employee" />
    </div>
  );
}

export default EmployeeCreate;
