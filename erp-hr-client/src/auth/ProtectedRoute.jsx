import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

// adminOnly mirrors @PreAuthorize("hasRole('HR_ADMIN')") on the backend
function ProtectedRoute({ children, adminOnly = false }) {
  const { isAuthenticated, isAdmin } = useAuth();

  // no token, or the token has expired -> back to login before rendering anything
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (adminOnly && !isAdmin) {
    return <p>You don't have permission to view this page.</p>;
  }

  return children;
}

export default ProtectedRoute;
