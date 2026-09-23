import { Link, useLocation } from 'react-router-dom';

// shown for unknown URLs instead of silently redirecting, so broken links are noticed (review 6.6)
function NotFound() {
  const { pathname } = useLocation();
  return (
    <div className="error-page">
      <h1>Page not found</h1>
      <p className="muted">There is no page at <code>{pathname}</code>.</p>
      <Link className="button-link" to="/dashboard">Back to the dashboard</Link>
    </div>
  );
}

export default NotFound;
