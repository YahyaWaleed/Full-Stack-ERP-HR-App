import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';

const COLUMNS = [
  { key: 'code', label: 'Code' },
  { key: 'nameEn', label: 'Name (EN)' },
  { key: 'nameAr', label: 'Name (AR)' },
  { key: 'branchName', label: 'Branch' },
  { key: 'parentDeptName', label: 'Parent Department' },
  { key: 'costCenter', label: 'Cost Center' },
  { key: 'employees', label: '', render: (d) => <Link to={`/dashboard/departments/${d.id}/employees`}>View Employees</Link> },
];

function DepartmentList() {
  const { data, error, loading } = useApi('/departments', { ttl: 300_000 });
  return (
    <div>
      <h1>Departments</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={data} loading={loading} />
    </div>
  );
}

export default DepartmentList;
