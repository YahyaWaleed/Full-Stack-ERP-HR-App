
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function BranchEmployees() {
  const { branchId } = useParams();
  const navigate = useNavigate();

  const [employees, setEmployees] = useState([]);
  const [branch, setBranch] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get(`/employees/branch/${branchId}`)
      .then(setEmployees)
      .catch((err) => setError(err.message));

    apiClient.get(`/branches/${branchId}`)
      .then(setBranch)
      .catch((err) => setError(err.message));
  }, [branchId]);

  return (
    <div>
      <h1> {branch && branch.nameEn} <h4>Branch Employees</h4></h1>

      

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <button onClick={() => navigate('/dashboard/branches')}>
        Back to Branches
      </button>

      <br />
      <br />

      {employees.length === 0 && !error ? (
        <p>No employees found in this branch.</p>
      ) : (
        <table border="1" cellPadding="8">
          <thead>
            <tr>
              <th>Employee Code</th>
              <th>Full Name</th>
              <th>Email</th>
              <th>Department</th>
              <th>Job Title</th>
              <th>Status</th>
            </tr>
          </thead>

          <tbody>
            {employees.map((employee) => (
              <tr key={employee.id}>
                <td>{employee.empCode}</td>
                <td>{employee.fullNameEn}</td>
                <td>{employee.email}</td>
                <td>{employee.departmentName || '-'}</td>
                <td>{employee.jobTitleName || '-'}</td>
                <td>{employee.empStatus || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default BranchEmployees;
