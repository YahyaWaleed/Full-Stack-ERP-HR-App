import { useNavigate, useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import ErrorMessage from '../../shared/components/ErrorMessage';
import EmployeeForm from './EmployeeForm';
import { employeesApi } from './api';

// sends the version it loaded: if someone else saved this employee meanwhile, the server answers 409
// ("changed by someone else") instead of silently overwriting their change (review 7.8)
function EmployeeEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data: employee, error } = useApi(`/employees/${id}`, { ttl: 0 });

  const save = async (form) => {
    await employeesApi.update(id, form);
    navigate(`/dashboard/employees/${id}`);
  };

  if (error) return <ErrorMessage error={error} />;
  if (!employee) return <p className="muted">Loading…</p>;

  return (
    <div>
      <h1>Edit {employee.fullNameEn} <span className="muted">({employee.empCode})</span></h1>
      <EmployeeForm key={employee.version} employee={employee} onSubmit={save} submitLabel="Save Changes" />
    </div>
  );
}

export default EmployeeEdit;
