import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

function ProtectedRoute({ children, adminOnly = false }) {
  const { role } = useAuth();
  const token = localStorage.getItem('token');

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (adminOnly && role !== 'HR_ADMIN') {
    return <p>You don't have permission to view this page.</p>;
  }

  return children;
}

export default ProtectedRoute;