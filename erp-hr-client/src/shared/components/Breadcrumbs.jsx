import { Link, useLocation } from 'react-router-dom';

// Dashboard / Employees / 5 / Contracts / Renew -- every part except the last is a link (review 6.7)
function Breadcrumbs({ labels }) {
  const { pathname } = useLocation();
  const segments = pathname.split('/').filter(Boolean);
  if (segments.length <= 1) return null;

  const crumbs = segments.map((segment, i) => ({
    to: '/' + segments.slice(0, i + 1).join('/'),
    label: labels[segment] ?? decodeURIComponent(segment),
  }));

  return (
    <nav className="breadcrumbs" aria-label="Breadcrumb">
      <ol>
        {crumbs.map((c, i) => (
          <li key={c.to}>
            {i < crumbs.length - 1 ? <Link to={c.to}>{c.label}</Link> : <span aria-current="page">{c.label}</span>}
          </li>
        ))}
      </ol>
    </nav>
  );
}

export default Breadcrumbs;
