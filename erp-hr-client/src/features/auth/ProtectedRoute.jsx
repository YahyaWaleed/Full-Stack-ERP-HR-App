import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './useAuth';

// adminOnly mirrors @PreAuthorize("hasRole('HR_ADMIN')") on the backend
function ProtectedRoute({ children, adminOnly = false }) {
  const { status, isAdmin } = useAuth();
  const location = useLocation();

  if (status === 'loading') {
    return <p className="muted page-loading">Loading…</p>;
  }
  if (status !== 'authenticated') {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />;
  }
  if (adminOnly && !isAdmin) {
    return (
      <div className="error-page">
        <h1>No access</h1>
        <p className="muted">You don't have permission to view this page.</p>
      </div>
    );
  }
  return children;
}

export default ProtectedRoute;
