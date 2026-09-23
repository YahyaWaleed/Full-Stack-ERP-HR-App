import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';
import { statusClass } from '../../shared/utils/statusClass';

const COLUMNS = [
  { key: 'empCode', label: 'Code' },
  { key: 'employeeName', label: 'Employee' },
  { key: 'leaveTypeName', label: 'Type' },
  { key: 'startDate', label: 'Start' },
  { key: 'endDate', label: 'End' },
  { key: 'daysCount', label: 'Working Days', align: 'right' },
  { key: 'status', label: 'Status', render: (r) => <span className={statusClass(r.status)}>{r.status}</span> },
  { key: 'view', label: '', render: (r) => <Link to={`/dashboard/leaves/${r.id}`}>View</Link> },
];

function LeaveRequestsList() {
  const [page, setPage] = useState(0);
  const { data, error, loading } = useApi(`/leaves?page=${page}&size=20&sort=appliedOn,desc`);

  return (
    <div>
      <h1>All Leave Requests</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={data?.content} loading={loading} emptyText="No leave requests." />
      <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
    </div>
  );
}

export default LeaveRequestsList;
