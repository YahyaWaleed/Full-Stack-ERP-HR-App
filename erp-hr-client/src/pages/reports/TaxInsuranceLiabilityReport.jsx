import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function TaxInsuranceLiabilityReport() {
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/reports/tax-insurance-liability').then(setRows).catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Tax & Insurance Liability</h1>
      {/* Print button */}
      <PrintButton />
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Period</th><th>Income Tax Due</th><th>Insurance (Employee)</th><th>Insurance (Employer)</th><th>Total Insurance</th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.periodCode}</td>
              <td>{r.incomeTaxDue}</td>
              <td>{r.insuranceEmployeeShare}</td>
              <td>{r.insuranceEmployerShare}</td>
              <td>{r.totalInsuranceDue}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TaxInsuranceLiabilityReport;