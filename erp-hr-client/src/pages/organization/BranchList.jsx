import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function BranchList() {
  const navigate = useNavigate();

  const [branches, setBranches] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/branches')
      .then(setBranches)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Branches</h1>

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Code</th>
            <th>Name (EN)</th>
            <th>Name (AR)</th>
            <th>City</th>
            <th>Country</th>
            <th>Active</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {branches.map((b) => (
            <tr key={b.id}>
              <td>{b.code}</td>
              <td>{b.nameEn}</td>
              <td>{b.nameAr}</td>
              <td>{b.city}</td>
              <td>{b.country}</td>
              <td>{b.active ? 'Yes' : 'No'}</td>

              <td>
                <button
                  onClick={() =>
                    navigate(`/dashboard/branches/${b.id}/employees`)
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

export default BranchList;