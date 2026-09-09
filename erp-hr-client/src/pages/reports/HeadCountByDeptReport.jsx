import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function HeadCountByDeptReport() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/reports/headcount-by-department')
      .then(setRows)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Headcount by Department</h1>

      {/* Print button */}
      <PrintButton />
      
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Department</th><th>Branch</th><th>Headcount</th><th>Avg Salary</th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.department}</td>
              <td>{r.branch}</td>
              <td>{r.headcount}</td>
              <td>{r.avgBasicSalary}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default HeadCountByDeptReport;