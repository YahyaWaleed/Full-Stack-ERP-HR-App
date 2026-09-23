import { Suspense } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import LiveClock from '../../shared/components/LiveClock';
import ThemeToggle from '../../shared/components/ThemeToggle';
import Breadcrumbs from '../../shared/components/Breadcrumbs';
import ErrorBoundary from '../../shared/components/ErrorBoundary';
import { useAuth } from '../auth/useAuth';
import { NAV_SECTIONS, SEGMENT_LABELS } from './navigation';

// the app shell: sidebar + breadcrumbs + the current page (lazy-loaded, isolated by an error boundary)
function DashboardLayout() {
  const { username, role, isAdmin, logout } = useAuth();
  const { pathname } = useLocation();

  return (
    <div className="app-shell">
      <LiveClock />
      <ThemeToggle />

      <aside className="sidebar">
        <h2>ERP HR App</h2>
        <p>Welcome, <strong>{username}</strong></p>
        <p className="muted small">{role === 'HR_ADMIN' ? 'HR Admin' : 'HR User'}</p>
        <button type="button" onClick={logout}>Logout</button>
        <hr />

        <nav className="side-nav" aria-label="Main">
          {NAV_SECTIONS.filter((s) => !s.adminOnly || isAdmin).map((section) => (
            <div key={section.title} className="side-nav-section">
              <p className="side-nav-title">{section.title}</p>
              {section.items.map((item) => (
                <NavLink key={item.to} to={item.to} end={item.end}
                         className={({ isActive }) => (isActive ? 'side-nav-link active' : 'side-nav-link')}>
                  {item.label}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>
      </aside>

      <main className="dashboard-content">
        <Breadcrumbs labels={SEGMENT_LABELS} />
        <ErrorBoundary key={pathname}>
          <Suspense fallback={<p className="muted">Loading…</p>}>
            <Outlet />
          </Suspense>
        </ErrorBoundary>
      </main>
    </div>
  );
}

export default DashboardLayout;
