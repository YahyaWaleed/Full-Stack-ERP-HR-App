import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { useAuth } from '../../auth/AuthContext';
import Pagination from '../../components/Pagination';

function PayrollPeriodDetails() {
  const { periodCode } = useParams();
  const { isAdmin } = useAuth();
  const [period, setPeriod] = useState(null);
  const [payslips, setPayslips] = useState([]);
  const [error, setError] = useState('');
  const [payMethod, setPayMethod] = useState('BANK');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const loadPeriod = () => {
    apiClient.get(`/payroll-periods/${periodCode}`)
      .then(setPeriod)
      .catch((err) => setError(err.message));
  };

  const loadPayslips = () => {
    apiClient.get(`/payslips/month/${periodCode}?page=${page}&size=20`)
      .then((data) => {
        setPayslips(data.content);
        setTotalPages(data.totalPages);
      })
      .catch(() => setPayslips([]));
  };

  useEffect(() => {
    loadPeriod();
    loadPayslips();
  }, [periodCode, page]);

  const handleRunPayroll = async () => {
    if (!window.confirm(`Run payroll for ${periodCode}? This will generate payslips for every employee.`)) return;
    try {
      await apiClient.post(`/payroll-periods/${periodCode}/run`);
      loadPeriod();
      loadPayslips();
    } catch (err) {
      setError(err.message);
    }
  };

  const handlePayPeriod = async () => {
    if (!window.confirm(`Mark ${periodCode} as paid via ${payMethod}?`)) return;
    try {
      await apiClient.post(`/payroll-periods/${periodCode}/pay?method=${payMethod}`);
      loadPeriod();
    } catch (err) {
      setError(err.message);
    }
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!period) return <p>Loading...</p>;

  return (
    <div>
      <h1>Period: {period.periodCode}</h1>
      <p><strong>Status:</strong> {period.status}</p>
      <p><strong>Fiscal Year:</strong> {period.fiscalYear}</p>
      <p><strong>Pay Date:</strong> {period.payDate}</p>

      {isAdmin && period.status === 'OPEN' && (
        <button onClick={handleRunPayroll}>Run Payroll</button>
      )}

      {isAdmin && period.status === 'PROCESSED' && (
        <div>
          <select value={payMethod} onChange={(e) => setPayMethod(e.target.value)}>
            <option value="BANK">Bank</option>
            <option value="CASH">Cash</option>
            <option value="CHEQUE">Cheque</option>
          </select>
          <button onClick={handlePayPeriod}>Pay Period</button>
        </div>
      )}

      <h3>Payslips</h3>
      {payslips.length === 0 ? (
        <p>No payslips generated yet — run payroll first.</p>
      ) : (
        <>
          <table border="1" cellPadding="8">
            <thead>
              <tr><th>Employee</th><th>Basic</th><th>Net Pay</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {payslips.map((p) => (
                <tr key={p.id}>
                  <td>{p.empCode}</td>
                  <td>{p.basicSalary}</td>
                  <td>{p.netPay}</td>
                  <td>{p.status}</td>
                  <td><Link to={`/dashboard/payroll/payslips/${p.id}`}>View</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
          <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}

export default PayrollPeriodDetails;