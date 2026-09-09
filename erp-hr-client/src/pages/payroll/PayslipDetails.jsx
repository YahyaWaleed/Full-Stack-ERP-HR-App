import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';

function PayslipDetails() {
  const { id } = useParams();
  const [payslip, setPayslip] = useState(null);
  const [lines, setLines] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get(`/payslips/${id}`).then(setPayslip).catch((err) => setError(err.message));
    apiClient.get(`/payslips/${id}/lines`).then(setLines).catch(() => {});
  }, [id]);

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!payslip) return <p>Loading...</p>;

  return (
    <div>
      <h1>Payslip #{payslip.payslipNo}</h1>
      <p><strong>Employee:</strong> {payslip.empCode} - {payslip.employeeName}</p>
      <p><strong>Period:</strong> {payslip.periodCode}</p>
      <p><strong>Basic Salary (EGP):</strong> {payslip.basicSalary}</p>
      <p><strong>Total Earnings (EGP):</strong> {payslip.totalEarnings}</p>
      <p><strong>Total Deductions (EGP):</strong> {payslip.totalDeductions}</p>
      <p><strong>Net Pay (EGP):</strong> {payslip.netPay}</p>
      <p>
        <strong>Status:</strong>{' '}
        <span style={{ fontWeight: 'bold', color: payslip.status === 'PAID' ? 'green' : 'inherit' }}>
          {payslip.status}
        </span>
      </p>

      <h3>Breakdown</h3>
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Component</th><th>Type</th><th>Amount (EGP)</th></tr>
        </thead>
        <tbody>
          {lines.map((line) => (
            <tr key={line.id}>
              <td>{line.compNameAr}</td>
              <td>{line.compType}</td>
              <td>{line.amount}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default PayslipDetails;