import { Link } from 'react-router-dom';
import { useApi } from '../../shared/api/useApi';
import ErrorMessage from '../../shared/components/ErrorMessage';

// the report menu is built from the backend's catalogue, so a new report appears here automatically
function ReportsIndex() {
  const { data: reports, error } = useApi('/reports', { ttl: 300_000 });
  return (
    <div>
      <h1>Reports</h1>
      <ErrorMessage error={error} />
      <ul className="link-grid">
        {(reports ?? []).map((r) => (
          <li key={r.slug}><Link to={`/dashboard/reports/${r.slug}`}>{r.title}</Link></li>
        ))}
      </ul>
    </div>
  );
}

export default ReportsIndex;
