import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';
import Pagination from '../../components/Pagination';

function EmployeeList() {
  const [employees, setEmployees] = useState([]);
  const [search, setSearch] = useState('');
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    apiClient.get(`/employees?page=${page}&size=20`)
      .then((data) => {
        setEmployees(data.content);
        setTotalPages(data.totalPages);
      })
      .catch((err) => setError(err.message));
  }, [page]);

  const filtered = employees.filter((emp) =>
    emp.empCode.toLowerCase().includes(search.toLowerCase()) ||
    emp.fullNameEn.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <h1>All Employees</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <input
        type="text"
        placeholder="Search by name or employee code..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Code</th>
            <th>Name</th>
            <th>Department</th>
            <th>Job Title</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {filtered.map((emp) => (
            <tr key={emp.id}>
              <td>{emp.empCode}</td>
              <td>{emp.fullNameEn}</td>
              <td>{emp.departmentName}</td>
              <td>{emp.jobTitleName}</td>
              <td><span className={statusClass(emp.empStatus)}>{emp.empStatus}</span></td>
              <td><Link to={`/dashboard/employees/${emp.id}`}>View</Link></td>
            </tr>
          ))}
        </tbody>
      </table>

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}

export default EmployeeList;