import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function PayrollPeriodDetails() {
  const { periodCode } = useParams();
  const [period, setPeriod] = useState(null);
  const [payslips, setPayslips] = useState([]);
  const [error, setError] = useState('');
  const [payMethod, setPayMethod] = useState('BANK');

  const loadPeriod = () => {
    apiClient.get(`/payroll-periods/${periodCode}`)
      .then(setPeriod)
      .catch((err) => setError(err.message));
  };

  const loadPayslips = () => {
    apiClient.get(`/payslips/month/${periodCode}`)
      .then(setPayslips)
      .catch(() => setPayslips([]));
  };

  useEffect(() => {
    loadPeriod();
    loadPayslips();
  }, [periodCode]);

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
      <p><strong>Status:</strong> <span className={statusClass(period.status)}>{period.status}</span></p>
      <p><strong>Fiscal Year:</strong> {period.fiscalYear}</p>
      <p><strong>Pay Date:</strong> {period.payDate}</p>

      {period.status === 'OPEN' && (
        <button onClick={handleRunPayroll}>Run Payroll</button>
      )}

      {period.status === 'PROCESSED' && (
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
        <table border="1" cellPadding="8">
          <thead>
            <tr><th>Employee</th><th>Basic Salary (EGP)</th><th>Net Pay (EGP)</th><th>Status</th><th></th></tr>
          </thead>
          <tbody>
            {payslips.map((p) => (
              <tr key={p.id}>
                <td>{p.empCode} - {p.employeeName}</td>
                <td>{p.basicSalary}</td>
                <td>{p.netPay}</td>
                <td><span className={statusClass(p.status)}>{p.status}</span></td>
                <td><Link to={`/dashboard/payroll/payslips/${p.id}`}>View</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default PayrollPeriodDetails;