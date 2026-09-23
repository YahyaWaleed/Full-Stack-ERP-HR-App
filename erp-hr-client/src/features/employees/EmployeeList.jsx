import { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';
import Pagination from '../../shared/components/Pagination';
import { statusClass } from '../../shared/utils/statusClass';
import { useDebounced } from '../../shared/utils/useDebounced';
import { EMPLOYEE_STATUSES } from './api';

// search and filters run on the server (GET /employees?q=&deptId=&status=), so this scales past one page
// of employees; typing is debounced, and derived values are memoised (review 8.2)
function EmployeeList({ fixedFilter = {} }) {
  const [search, setSearch] = useState('');
  const [deptId, setDeptId] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const q = useDebounced(search.trim());

  const { data: departments = [] } = useApi('/departments', { ttl: 300_000 });

  const path = useMemo(() => {
    const params = new URLSearchParams({ page: String(page), size: '20', ...fixedFilter });
    if (q) params.set('q', q);
    if (deptId) params.set('deptId', deptId);
    if (status) params.set('status', status);
    return `/employees?${params}`;
  }, [page, q, deptId, status, fixedFilter]);

  const { data, error, loading } = useApi(path);

  const columns = useMemo(() => [
    { key: 'empCode', label: 'Code' },
    { key: 'fullNameEn', label: 'Name' },
    { key: 'departmentName', label: 'Department' },
    { key: 'jobTitleName', label: 'Job Title' },
    { key: 'branchName', label: 'Branch' },
    { key: 'empStatus', label: 'Status', render: (e) => <span className={statusClass(e.empStatus)}>{e.empStatus}</span> },
    { key: 'view', label: '', render: (e) => <Link to={`/dashboard/employees/${e.id}`}>View</Link> },
  ], []);

  // any filter change starts again from the first page
  const onFilter = (setter) => (e) => {
    setter(e.target.value);
    setPage(0);
  };

  return (
    <div>
      <h1>All Employees</h1>
      <ErrorMessage error={error} />

      <div className="filters">
        <input type="search" placeholder="Search by name, code or national ID…" value={search}
               onChange={onFilter(setSearch)} aria-label="Search employees" />
        {!fixedFilter.deptId && (
          <select value={deptId} onChange={onFilter(setDeptId)} aria-label="Department">
            <option value="">All departments</option>
            {departments.map((d) => <option key={d.id} value={d.id}>{d.nameEn}</option>)}
          </select>
        )}
        <select value={status} onChange={onFilter(setStatus)} aria-label="Status">
          <option value="">Any status</option>
          {EMPLOYEE_STATUSES.map((s) => <option key={s} value={s}>{s}</option>)}
        </select>
      </div>

      <DataTable columns={columns} rows={data?.content} loading={loading} emptyText="No employees match these filters." />
      {data && <p className="muted small">{data.totalElements} employee(s)</p>}
      <Pagination page={page} totalPages={data?.totalPages ?? 0} onPageChange={setPage} />
    </div>
  );
}

export default EmployeeList;
