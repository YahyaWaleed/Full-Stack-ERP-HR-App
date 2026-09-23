import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getToken, setToken, clearToken, decodeToken, isTokenValid } from './tokenStorage';
import { setUnauthorizedHandler } from '../api/apiClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const navigate = useNavigate();
  // read once on first render (not in an effect) so role-guarded routes don't flash "no permission" on refresh
  const [token, setTokenState] = useState(() => (isTokenValid(getToken()) ? getToken() : null));

  const decoded = token ? decodeToken(token) : null;
  const username = decoded?.sub ?? null;
  const role = decoded?.role ?? null;

  const login = (newToken) => {
    setToken(newToken);
    setTokenState(newToken);
    navigate('/dashboard');
  };

  const logout = useCallback(() => {
    clearToken();
    setTokenState(null);
    navigate('/login');
  }, [navigate]);

  // when any API call comes back 401, apiClient calls logout so the in-memory state is cleared too
  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  const isAuthenticated = isTokenValid(token);
  const isAdmin = isAuthenticated && role === 'HR_ADMIN';

  return (
    <AuthContext.Provider value={{ token, username, role, isAuthenticated, isAdmin, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
