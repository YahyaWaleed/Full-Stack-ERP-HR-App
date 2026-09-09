import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function LoanDetails() {
  const { id } = useParams();
  const [loan, setLoan] = useState(null);
  const [installments, setInstallments] = useState([]);
  const [error, setError] = useState('');

  const loadLoan = () => {
    apiClient.get(`/loans/${id}`).then(setLoan).catch((err) => setError(err.message));
    apiClient.get(`/loans/${id}/installments`).then(setInstallments).catch(() => {});
  };

  useEffect(loadLoan, [id]);

  const handleClose = async () => {
    if (!window.confirm('Close this loan early (manual payoff)?')) return;
    try {
      await apiClient.patch(`/loans/${id}/close`);
      loadLoan();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCancel = async () => {
    if (!window.confirm('Cancel this loan?')) return;
    try {
      await apiClient.patch(`/loans/${id}/cancel`);
      loadLoan();
    } catch (err) {
      setError(err.message);
    }
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!loan) return <p>Loading...</p>;

  return (
    <div>
      <h1>Loan #{loan.id}</h1>
      <p><strong>Employee:</strong> {loan.empCode}</p>
      <p><strong>Type:</strong> {loan.type}</p>
      <p><strong>Principal:</strong> {loan.principalAmount}</p>
      <p><strong>Remaining Balance:</strong> {loan.remainingBalance}</p>
      <p><strong>Status:</strong> <span className={statusClass(loan.status)}>{loan.status}</span></p>
      <p><strong>Approved By:</strong> {loan.approvedById || 'Not yet approved'}</p>

      {loan.status === 'ACTIVE' && (
        <>
          <button onClick={handleClose}>Close Early (Payoff)</button>{' '}
          <button onClick={handleCancel}>Cancel Loan</button>
        </>
      )}

      <h3>Installments</h3>
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Period</th><th>Amount</th><th>Paid On</th></tr>
        </thead>
        <tbody>
          {installments.map((inst) => (
            <tr key={inst.id}>
              <td>{inst.periodCode}</td>
              <td>{inst.amount}</td>
              <td>{inst.paidOn || 'Not yet paid'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LoanDetails;