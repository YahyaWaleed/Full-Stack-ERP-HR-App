import { createContext, useContext } from 'react';

// kept apart from AuthProvider so that file only exports a component (fast refresh)
export const AuthContext = createContext(null);

// { status: 'loading' | 'authenticated' | 'anonymous', username, role, isAdmin, login(), logout() }
export function useAuth() {
  return useContext(AuthContext);
}
