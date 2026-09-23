import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { statusClass } from '../../shared/utils/statusClass';
import { useAuth } from '../auth/useAuth';
import { leavesApi } from './api';

function LeaveRequestDetails() {
  const { id } = useParams();
  const { isAdmin } = useAuth();
  const { data: request, error: loadError } = useApi(`/leaves/${id}`);
  const [rejectReason, setRejectReason] = useState('');
  const [showRejectForm, setShowRejectForm] = useState(false);
  const [error, setError] = useState(null);

  const run = (action) => async () => {
    setError(null);
    try {
      await action();
      setShowRejectForm(false);
    } catch (err) {
      setError(err); // e.g. 409 "not enough remaining leave balance" or "already decided"
    }
  };

  if (loadError) return <ErrorMessage error={loadError} />;
  if (!request) return <p className="muted">Loading…</p>;

  return (
    <div>
      <h1>Leave Request #{request.id}</h1>
      <ErrorMessage error={error} />
      <dl className="details">
        <dt>Employee</dt><dd>{request.empCode} — {request.employeeName}</dd>
        <dt>Type</dt><dd>{request.leaveTypeName}</dd>
        <dt>Dates</dt><dd>{request.startDate} to {request.endDate} ({request.daysCount} working days)</dd>
        <dt>Reason</dt><dd>{request.reason || '—'}</dd>
        {request.attachmentRef && <><dt>Document</dt><dd>{request.attachmentRef}</dd></>}
        <dt>Status</dt><dd><span className={statusClass(request.status)}>{request.status}</span></dd>
        {request.decidedOn && <><dt>Decided On</dt><dd>{request.decidedOn}</dd></>}
        {request.rejectReason && <><dt>Rejected Because</dt><dd>{request.rejectReason}</dd></>}
      </dl>

      {isAdmin && request.status === 'PENDING' && (
        <div className="page-actions">
          <button className="btn-approve" type="button" onClick={run(() => leavesApi.approve(id))}>Approve</button>
          <button className="btn-reject" type="button" onClick={() => setShowRejectForm(true)}>Reject</button>
          <button type="button" onClick={() => window.confirm('Cancel this leave request?') && run(() => leavesApi.cancel(id))()}>
            Cancel
          </button>
        </div>
      )}

      {showRejectForm && (
        <form className="inline-form" onSubmit={(e) => { e.preventDefault(); run(() => leavesApi.reject(id, rejectReason))(); }}>
          <input aria-label="Reason for rejection" placeholder="Reason for rejection" value={rejectReason} required maxLength={200}
                 onChange={(e) => setRejectReason(e.target.value)} />
          <button type="submit" className="btn-reject">Confirm Reject</button>
        </form>
      )}
    </div>
  );
}

export default LeaveRequestDetails;
