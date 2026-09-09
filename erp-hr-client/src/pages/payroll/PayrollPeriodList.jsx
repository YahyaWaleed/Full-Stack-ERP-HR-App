import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function PayrollPeriodList() {
  const [periods, setPeriods] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/payroll-periods')
      .then(setPeriods)
      .catch((err) => setError(err.message));
  }, []);

  return (
    <div>
      <h1>Payroll Periods</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Period Code</th>
            <th>Fiscal Year</th>
            <th>Pay Date</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {periods.map((period) => (
            <tr key={period.id}>
              <td>{period.periodCode}</td>
              <td>{period.fiscalYear}</td>
              <td>{period.payDate}</td>
              <td><span className={statusClass(period.status)}>{period.status}</span></td>
              <td><Link to={`/dashboard/payroll/periods/${period.periodCode}`}>Open</Link></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default PayrollPeriodList;