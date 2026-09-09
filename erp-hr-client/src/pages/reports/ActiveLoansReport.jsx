import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function ActiveLoansReport() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/reports/active-loans').then(setRows).catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Active Loans</h1>
       {/* Print button */}
      <PrintButton />
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Employee</th><th>Type</th><th>Principal</th><th>Remaining</th><th>Paid %</th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.fullNameAr}</td>
              <td>{r.loanType}</td>
              <td>{r.principalAmount}</td>
              <td>{r.remainingBalance}</td>
              <td>{r.paidPct}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ActiveLoansReport;