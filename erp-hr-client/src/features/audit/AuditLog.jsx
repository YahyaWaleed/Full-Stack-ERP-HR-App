import { useState } from 'react';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';

const COLUMNS = [
  { key: 'occurredAt', label: 'When', render: (e) => e.occurredAt.replace('T', ' ').slice(0, 19) },
  { key: 'actor', label: 'Who' },
  { key: 'action', label: 'Action' },
  { key: 'target', label: 'Record', render: (e) => `${e.targetType} ${e.targetId}` },
  { key: 'details', label: 'Details' },
];

// who ran payroll, terminated an employee, changed a salary, ... (review 9.9) -- HR_ADMIN only
function AuditLog() {
  const [page, setPage] = useState(0);
  const { data, error, loading } = useApi(`/audit?page=${page}&size=50`, { ttl: 0 });
  return (
    <div>
      <h1>Audit Log</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={data?.content} loading={loading} emptyText="Nothing recorded yet." />
      <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
    </div>
  );
}

export default AuditLog;
