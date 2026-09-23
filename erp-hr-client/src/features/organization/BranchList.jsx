import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';

const COLUMNS = [
  { key: 'code', label: 'Code' },
  { key: 'nameEn', label: 'Name (EN)' },
  { key: 'nameAr', label: 'Name (AR)' },
  { key: 'city', label: 'City' },
  { key: 'country', label: 'Country' },
  { key: 'active', label: 'Active' },
  { key: 'employees', label: '', render: (b) => <Link to={`/dashboard/branches/${b.id}/employees`}>View Employees</Link> },
];

function BranchList() {
  const { data, error, loading } = useApi('/branches', { ttl: 300_000 });
  return (
    <div>
      <h1>Branches</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={data} loading={loading} />
    </div>
  );
}

export default BranchList;
