import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function OvertimeTop10Report() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/reports/overtime-top10').then(setRows).catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Top Overtime Earners</h1>
      {/* Print button */}
      <PrintButton />
      {error && <p style={{ color: 'red' }}>{error}</p>}
     <table border="1" cellPadding="8">
      <thead>
        <tr>
          <th>Rank</th>
          <th>Employee</th>
          <th>Overtime Hours</th>
          <th>Overtime Paid</th>
        </tr>
      </thead>

      <tbody>
        {rows.map((r, i) => (
          <tr key={i}>
            <td>{i + 1}</td>
            <td>{r.fullNameAr}</td>
            <td>{r.otHours}</td>
            <td>{r.otPaid}</td>
          </tr>
        ))}
      </tbody>
    </table>
    </div>
  );
}

export default OvertimeTop10Report;