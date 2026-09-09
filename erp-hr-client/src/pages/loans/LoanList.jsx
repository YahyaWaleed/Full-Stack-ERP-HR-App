
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { statusClass } from '../../utils/statusClass';

function LoanList() {
  const [loans, setLoans] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadData = async () => {
      try {
        const [loansData, employeesData] = await Promise.all([
          apiClient.get('/loans'),
          apiClient.get('/employees'),
        ]);

        setLoans(loansData);
        setEmployees(employeesData);
      } catch (err) {
        setError(err.message);
      }
    };

    loadData();
  }, []);

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
            const employee = employees.find(
              (employee) => employee.id === loan.empId
            );

            return (
              <tr key={loan.id}>
                <td>
                  {employee
                    ? `${employee.empCode} ${employee.fullNameEn}`
                    : 'Unknown Employee'}
                </td>

                <td>{loan.type}</td>

                <td>{loan.principalAmount}</td>

                <td>{loan.remainingBalance}</td>

                <td>
                  <span className={statusClass(loan.status)}>
                    {loan.status}
                  </span>
                </td>

                <td>
                  <Link to={`/dashboard/loans/${loan.id}`}>
                    View
                  </Link>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default LoanList;

