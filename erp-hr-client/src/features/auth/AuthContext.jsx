import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as client from '../../shared/api/client';
import { clearApiCache } from '../../shared/api/useApi';
import { AuthContext } from './useAuth';

// The single source of truth for who is logged in (review 6.1). The access token itself stays in
// shared/api/tokenStorage (memory only); a page reload restores the session from the refresh cookie.
export function AuthProvider({ children }) {
  const navigate = useNavigate();
  const [session, setSession] = useState(null);        // { username, role } once logged in
  const [status, setStatus] = useState('loading');      // until the first refresh attempt answers

  // on start-up: is there still a valid refresh cookie?
  useEffect(() => {
    let active = true;
    client.refreshSession().then((s) => {
      if (!active) return;
      setSession(s ? { username: s.username, role: s.role } : null);
      setStatus(s ? 'authenticated' : 'anonymous');
    });
    return () => {
      active = false;
    };
  }, []);

  const endSession = useCallback(() => {
    clearApiCache();
    setSession(null);
    setStatus('anonymous');
    navigate('/login', { replace: true });
  }, [navigate]);

  // any API call that can't refresh an expired session lands here
  useEffect(() => {
    client.setSessionExpiredHandler(endSession);
    return () => client.setSessionExpiredHandler(null);
  }, [endSession]);

  const login = useCallback(async (username, password) => {
    const s = await client.login(username, password); // throws ApiError on 401/429
    clearApiCache();
    setSession({ username: s.username, role: s.role });
    setStatus('authenticated');
    navigate('/dashboard');
  }, [navigate]);

  const logout = useCallback(async () => {
    await client.logout().catch(() => {});
    endSession();
  }, [endSession]);

  const value = useMemo(() => ({
    status,
    username: session?.username ?? null,
    role: session?.role ?? null,
    isAuthenticated: status === 'authenticated',
    isAdmin: status === 'authenticated' && session?.role === 'HR_ADMIN',
    login,
    logout,
  }), [status, session, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
