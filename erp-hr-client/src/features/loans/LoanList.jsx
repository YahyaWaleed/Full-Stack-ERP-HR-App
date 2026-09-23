import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';
import { statusClass } from '../../shared/utils/statusClass';

// the employee's code and name now come with each loan -- no second download of every employee to look them up
const COLUMNS = [
  { key: 'employee', label: 'Employee', render: (l) => `${l.empCode} ${l.employeeName}` },
  { key: 'type', label: 'Type' },
  { key: 'principalAmount', label: 'Principal', align: 'right' },
  { key: 'monthlyInstallment', label: 'Monthly', align: 'right' },
  { key: 'remainingBalance', label: 'Remaining', align: 'right' },
  { key: 'status', label: 'Status', render: (l) => <span className={statusClass(l.status)}>{l.status}</span> },
  { key: 'view', label: '', render: (l) => <Link to={`/dashboard/loans/${l.id}`}>View</Link> },
];

function LoanList() {
  const [page, setPage] = useState(0);
  const { data, error, loading } = useApi(`/loans?page=${page}&size=20&sort=requestDate,desc`);

  return (
    <div>
      <h1>All Loans</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={data?.content} loading={loading} emptyText="No loans." />
      <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
    </div>
  );
}

export default LoanList;
