import { useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import PrintButton from '../../components/PrintButton';

function ContractsExpiringReport() {
  const [months, setMonths] = useState('12');
  const [rows, setRows] = useState([]);
  const [error, setError] = useState('');
  

  const handleRun = async () => {
    try {
      const data = await apiClient.get(`/reports/contracts-expiring?months=${months}`);
      setRows(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  // pulls the numeric employee id out of "EMP-0042" -> 42
  const getEmpIdFromCode = (empCode) => parseInt(empCode.split('-')[1], 10);

  return (
    <div>
      <h1>Contracts Expiring</h1>

      {/* Print button */}
      <PrintButton />

      {error && <p style={{ color: 'red' }}>{error}</p>}

      <label>Within how many months?</label><br />
      <input value={months} onChange={(e) => setMonths(e.target.value)} />
      <button onClick={handleRun}>Run Report</button>

      <table border="1" cellPadding="8">
        <thead>
          <tr><th>Employee</th><th>Contract No</th><th>Type</th><th>End Date</th><th>Days Left</th><th></th></tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i}>
              <td>{r.fullNameAr}</td>
              <td>{r.contractNo}</td>
              <td>{r.contractType}</td>
              <td>{r.endDate}</td>
              <td>{r.daysLeft}</td>
              <td>
                {r.daysLeft <= 365 && (
                  <Link to={`/dashboard/employees/${getEmpIdFromCode(r.empCode)}/contracts/renew`}>Renew Contract</Link>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ContractsExpiringReport;