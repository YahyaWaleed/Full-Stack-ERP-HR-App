import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import { statusClass } from '../../shared/utils/statusClass';
import { useAuth } from '../auth/useAuth';
import { loansApi } from './api';

function LoanDetails() {
  const { id } = useParams();
  const { isAdmin } = useAuth();
  const { data: loan, error: loadError } = useApi(`/loans/${id}`);
  const { data: installments } = useApi(`/loans/${id}/installments`);
  const [error, setError] = useState(null);

  const act = (question, action) => async () => {
    if (!window.confirm(question)) return;
    setError(null);
    try {
      await action();
    } catch (err) {
      setError(err); // 409 if the loan is no longer ACTIVE
    }
  };

  if (loadError) return <ErrorMessage error={loadError} />;
  if (!loan) return <p className="muted">Loading…</p>;

  return (
    <div>
      <h1>Loan #{loan.id}</h1>
      <ErrorMessage error={error} />
      <dl className="details">
        <dt>Employee</dt><dd><Link to={`/dashboard/employees/${loan.empId}`}>{loan.empCode} — {loan.employeeName}</Link></dd>
        <dt>Type</dt><dd>{loan.type}</dd>
        <dt>Principal</dt><dd>{loan.principalAmount}</dd>
        <dt>Installments</dt><dd>{loan.installmentsCount} × {loan.monthlyInstallment} from {loan.startPeriod}</dd>
        <dt>Remaining Balance</dt><dd>{loan.remainingBalance}</dd>
        <dt>Status</dt><dd><span className={statusClass(loan.status)}>{loan.status}</span></dd>
        <dt>Approved By</dt><dd>{loan.approvedByName || '—'}</dd>
      </dl>

      {isAdmin && loan.status === 'ACTIVE' && (
        <div className="page-actions">
          <button className="btn-approve" type="button" onClick={act('Close this loan early (paid off in full)?', () => loansApi.close(id))}>
            Close Early (Payoff)
          </button>
          <button className="btn-reject" type="button" onClick={act('Cancel this loan?', () => loansApi.cancel(id))}>Cancel Loan</button>
        </div>
      )}

      <h3>Installments</h3>
      <DataTable rows={installments} emptyText="No installments deducted yet." columns={[
        { key: 'periodCode', label: 'Period' },
        { key: 'amount', label: 'Amount', align: 'right' },
        { key: 'paid', label: 'Paid', render: (i) => (i.paidOn ? `Paid ${i.paidOn}` : 'Deducted, not paid yet') },
      ]} />
    </div>
  );
}

export default LoanDetails;
