import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';

function LeaveBalanceList() {
  const [employees, setEmployees] = useState([]);
  const [search, setSearch] = useState('');
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [balances, setBalances] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    apiClient.get('/employees')
      .then(setEmployees)
      .catch((err) => setError(err.message));
  }, []);

  const matches = search.trim()
    ? employees.filter((e) =>
        e.empCode.toLowerCase().includes(search.toLowerCase()) ||
        e.fullNameEn.toLowerCase().includes(search.toLowerCase())
      )
    : [];

  const handleSelect = async (employee) => {
    setSelectedEmployee(employee);
    setSearch('');
    try {
      const data = await apiClient.get(`/employees/${employee.id}/leave-balances`);
      setBalances(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Leave Balances</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <label>Search by Employee Code or Name</label><br />
      <input
        placeholder="e.g. EMP-0001 or Ahmed"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {matches.length > 0 && (
        <ul className="search-results">
          {matches.map((emp) => (
            <li key={emp.id} onClick={() => handleSelect(emp)}>
              {emp.empCode} — {emp.fullNameEn}
            </li>
          ))}
        </ul>
      )}

      {selectedEmployee && (
        <>
          <h3>{selectedEmployee.empCode} — {selectedEmployee.fullNameEn}</h3>

          <table border="1" cellPadding="8">
            <thead>
              <tr>
                <th>Leave Type</th>
                <th>Fiscal Year</th>
                <th>Entitled</th>
                <th>Carried Forward</th>
                <th>Used</th>
                <th>Remaining</th>
              </tr>
            </thead>
            <tbody>
              {balances.map((b) => (
                <tr key={b.id}>
                  <td>{b.leaveTypeName}</td>
                  <td>{b.fiscalYear}</td>
                  <td>{b.entitledDays}</td>
                  <td>{b.carriedForward}</td>
                  <td>{b.usedDays}</td>
                  <td>{b.remainingDays}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

export default LeaveBalanceList;