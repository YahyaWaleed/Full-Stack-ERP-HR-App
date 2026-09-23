import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { useAuth } from '../../auth/AuthContext';
import { statusClass } from '../../utils/statusClass';

function LeaveRequestDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [request, setRequest] = useState(null);
  const [error, setError] = useState('');
  const [rejectReason, setRejectReason] = useState('');
  const [showRejectForm, setShowRejectForm] = useState(false);

  const loadRequest = () => {
    apiClient.get(`/leaves/${id}`).then(setRequest).catch((err) => setError(err.message));
  };

  useEffect(loadRequest, [id]);

  const handleApprove = async () => {
    try {
      await apiClient.post(`/leaves/${id}/approve`);
      loadRequest();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleReject = async () => {
    try {
      await apiClient.post(`/leaves/${id}/reject`, { rejectReason });
      setShowRejectForm(false);
      loadRequest();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('Cancel this leave request?')) return;
    try {
      await apiClient.post(`/leaves/${id}/cancel`);
      loadRequest();
    } catch (err) {
      setError(err.message);
    }
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!request) return <p>Loading...</p>;

  return (
    <div>
      <h1>Leave Request #{request.id}</h1>
      <p><strong>Employee:</strong> {request.empCode}  {request.employeeName}</p>
      <p><strong>Type:</strong> {request.leaveTypeName}</p>
      <p><strong>Dates:</strong> {request.startDate} to {request.endDate} ({request.daysCount} days)</p>
      <p><strong>Reason:</strong> {request.reason}</p>
      <p><strong>Status:</strong> <span className={statusClass(request.status)}>{request.status}</span></p>
      {request.rejectReason && <p><strong>Rejected because:</strong> {request.rejectReason}</p>}

      {isAdmin && request.status === 'PENDING' && (
  <>
    <button className="btn-approve" onClick={handleApprove}>Approve</button>{' '}
    <button className="btn-reject" onClick={() => setShowRejectForm(true)}>Reject</button>{' '}
    <button onClick={handleCancel}>Cancel</button>

    {showRejectForm && (
      <div>
        <input
          placeholder="Reason for rejection"
          value={rejectReason}
          onChange={(e) => setRejectReason(e.target.value)}
        />
        <button onClick={handleReject}>Confirm Reject</button>
      </div>
    )}
  </>
)}
    </div>
  );
}

export default LeaveRequestDetails;