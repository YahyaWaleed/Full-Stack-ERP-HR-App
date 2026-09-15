import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';
import Pagination from '../../components/Pagination';

function LoanList() {
  const [loans, setLoans] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [error, setError] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [loansPage, employeesPage] = await Promise.all([
          apiClient.get(`/loans?page=${page}&size=20`),
          apiClient.get('/employees?size=1000'), // large size to get the full list for name lookups
        ]);

        setLoans(loansPage.content);
        setTotalPages(loansPage.totalPages);
        setEmployees(employeesPage.content);
      } catch (err) {
        setError(err.message);
      }
    };

    loadData();
  }, [page]);

  return (
    <div>
      <h1>All Loans</h1>
      {error && <p style={{ color: 'red' }}>{error}</p>}

      <table border="1" cellPadding="8">
        <thead>
          <tr>
            <th>Employee</th>
            <th>Type</th>
            <th>Principal</th>
            <th>Remaining Balance</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {loans.map((loan) => {
            const employee = employees.find((e) => e.id === loan.empId);
            return (
              <tr key={loan.id}>
                <td>{employee ? `${employee.empCode} ${employee.fullNameEn}` : 'Unknown Employee'}</td>
                <td>{loan.type}</td>
                <td>{loan.principalAmount}</td>
                <td>{loan.remainingBalance}</td>
                <td><span className={statusClass(loan.status)}>{loan.status}</span></td>
                <td><Link to={`/dashboard/loans/${loan.id}`}>View</Link></td>
              </tr>
            );
          })}
        </tbody>
      </table>

      <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
    </div>
  );
}

export default LoanList;