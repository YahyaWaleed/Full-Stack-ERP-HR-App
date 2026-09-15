import { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const AuthContext = createContext(null);

// reads the payload out of a JWT without needing any extra library
function decodeToken(token) {
  try {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const navigate = useNavigate();
  const [username, setUsername] = useState(null);
  const [role, setRole] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded = decodeToken(token);
      if (decoded) {
        setUsername(decoded.sub);
        setRole(decoded.role);
      }
    }
  }, []);

  const login = (token) => {
    localStorage.setItem('token', token);
    const decoded = decodeToken(token);
    if (decoded) {
      setUsername(decoded.sub);
      setRole(decoded.role);
    }
    navigate('/dashboard');
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUsername(null);
    setRole(null);
    navigate('/login');
  };

  const isAdmin = role === 'HR_ADMIN';

  return (
    <AuthContext.Provider value={{ username, role, isAdmin, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}