import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import DataTable from '../../shared/components/DataTable';
import ErrorMessage from '../../shared/components/ErrorMessage';

const COLUMNS = [
  { key: 'code', label: 'Code' },
  { key: 'titleEn', label: 'Title (EN)' },
  { key: 'titleAr', label: 'Title (AR)' },
  { key: 'jobGrade', label: 'Grade' },
  { key: 'minSalary', label: 'Min Salary', align: 'right' },
  { key: 'maxSalary', label: 'Max Salary', align: 'right' },
  { key: 'managerial', label: 'Managerial' },
  { key: 'employees', label: '', render: (j) => <Link to={`/dashboard/jobs/${j.id}/employees`}>View Employees</Link> },
];

function JobTitleList() {
  const { data, error, loading } = useApi('/jobs', { ttl: 300_000 });
  return (
    <div>
      <h1>Job Titles</h1>
      <ErrorMessage error={error} />
      <DataTable columns={COLUMNS} rows={data} loading={loading} />
    </div>
  );
}

export default JobTitleList;
