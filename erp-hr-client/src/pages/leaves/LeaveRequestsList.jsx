import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function LeaveRequestsList() {
  const [requests, setRequests] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/leaves')
      .then((data) => {
        const statusOrder = {
          PENDING: 1,
          REJECTED: 2,
          APPROVED: 3
        };

        const sortedRequests = [...data].sort(
          (a, b) => statusOrder[a.status] - statusOrder[b.status]
        );

        setRequests(sortedRequests);
      })
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>All Leave Requests</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Employee</th>
            <th>Type</th>
            <th>Start</th>
            <th>End</th>
            <th>Days</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {requests.map((req) => (
            <tr key={req.id}>
              <td>{req.empCode} {req.employeeName}</td>
              <td>{req.leaveTypeName}</td>
              <td>{req.startDate}</td>
              <td>{req.endDate}</td>
              <td>{req.daysCount}</td>
              <td><span className={statusClass(req.status)}>{req.status}</span></td>
              <td><Link to={`/dashboard/leaves/${req.id}`}>View</Link></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LeaveRequestsList;