import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import { useNavigate } from 'react-router-dom';

function DepartmentList() {
  const navigate = useNavigate();
  const [departments, setDepartments] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/departments')
      .then(setDepartments)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Departments</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Code</th>
            <th>Name (EN)</th>
            <th>Name (AR)</th>
            <th>Branch</th>
            <th>Parent Department</th>
            <th>Cost Center</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {departments.map((d) => (
            <tr key={d.id}>
              <td>{d.code}</td>
              <td>{d.nameEn}</td>
              <td>{d.nameAr}</td>
              <td>{d.branchName}</td>
              <td>{d.parentDeptName || '—'}</td>
              <td>{d.costCenter}</td>
              <td>
                <button
                  onClick={() =>
                    navigate(`/dashboard/departments/${d.id}/employees`)
                  }
                >
                  View Employees
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default DepartmentList;