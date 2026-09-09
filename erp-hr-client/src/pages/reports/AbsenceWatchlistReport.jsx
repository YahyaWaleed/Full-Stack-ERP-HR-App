import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function AbsenceWatchlistReport() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/reports/absence-watchlist').then(setRows).catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Absence Watch-list</h1>
      
      {/* Print button */}
      <PrintButton />
      {error && <p style={{ color: 'red' }}>{error}</p>}
      
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Employee</th><th>Department</th><th>Unpaid Days</th><th>Late Minutes</th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.fullNameAr}</td>
              <td>{r.department}</td>
              <td>{r.unpaidDays}</td>
              <td>{r.lateMinutes}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AbsenceWatchlistReport;