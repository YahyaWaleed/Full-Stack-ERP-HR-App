import { useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function LeaveRequestLogReport() {
  const [status, setStatus] = useState('');
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  const handleRun = async () => {
    try {
      const query = status ? `?status=${status}` : '';
      const data = await apiClient.get(`/reports/leave-requests${query}`);
      setRows(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Leave Request Log</h1>

      {/* Print button */}
      <PrintButton />
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <label>Status (optional)</label><br />
      <select value={status} onChange={(e) => setStatus(e.target.value)}>
        <option value="">All</option>
        <option value="PENDING">Pending</option>
        <option value="APPROVED">Approved</option>
        <option value="REJECTED">Rejected</option>
        <option value="CANCELLED">Cancelled</option>
      </select>
      <button onClick={handleRun}>Run Report</button>

      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Employee</th><th>Type</th><th>Start</th><th>End</th><th>Days</th><th>Status</th><th>Reason</th><th></th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.fullNameAr}</td>
              <td>{r.leaveType}</td>
              <td>{r.startDate}</td>
              <td>{r.endDate}</td>
              <td>{r.daysCount}</td>
              <td>{r.status}</td>
              <td>{r.reason}</td>
              <td>
                {r.status === 'PENDING' && (
                  <Link to={`/dashboard/leaves/${r.requestId}`}>Manage</Link>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LeaveRequestLogReport;