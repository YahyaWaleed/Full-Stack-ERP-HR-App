import { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';

function AttendanceList() {
  const [employees, setEmployees] = useState([]);
  const [search, setSearch] = useState('');
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [records, setRecords] = useState([]);
  const [error, setError] = useState('');

  // load the full employee list once, so we can search it locally
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
      const data = await apiClient.get(`/employees/${employee.id}/attendance`);
      setRecords(data);
      setError('');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h1>Attendance Sheet</h1>
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
                <th>Period</th>
                <th>Working Days</th>
                <th>Present Days</th>
                <th>Paid Leave Days</th>
                <th>Unpaid Absent Days</th>
                <th>Overtime Hours</th>
                <th>Late Minutes</th>
              </tr>
            </thead>
            <tbody>
              {records.map((r) => (
                <tr key={r.id}>
                  <td>{r.periodCode}</td>
                  <td>{r.workingDays}</td>
                  <td>{r.presentDays}</td>
                  <td>{r.paidLeaveDays}</td>
                  <td>{r.unpaidAbsentDays}</td>
                  <td>{r.overtimeHours}</td>
                  <td>{r.lateMinutes}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

export default AttendanceList;