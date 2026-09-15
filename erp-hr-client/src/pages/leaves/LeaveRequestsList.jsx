import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';
import Pagination from '../../components/Pagination';

function LeaveRequestsList() {
  const [requests, setRequests] = useState([]);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    apiClient.get(`/leaves?page=${page}&size=20`)
      .then((data) => {
        setRequests(data.content);
        setTotalPages(data.totalPages);
      })
      .catch((err) => setError(err.message));
  }, [page]);

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
              <td>{req.employeeName}</td>
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

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}

export default LeaveRequestsList;