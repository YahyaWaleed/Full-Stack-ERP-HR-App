import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function EmployeeDirectoryReport() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/reports/employee-directory').then(setRows).catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Employee Directory</h1>

      {/* Print button */}
      <PrintButton /> 
      
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Code</th><th>Name</th><th>Dept</th><th>Job Title</th>
            <th>Status</th><th>Hire Date</th><th>Salary</th><th>Email</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.empCode}</td>
              <td>{r.fullNameEn}</td>
              <td>{r.department}</td>
              <td>{r.jobTitle}</td>
              <td>{r.empStatus}</td>
              <td>{r.hireDate}</td>
              <td>{r.basicSalary}</td>
              <td>{r.email}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default EmployeeDirectoryReport;