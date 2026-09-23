import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import ThemeToggle from '../../shared/components/ThemeToggle';
import { useAuth } from './useAuth';
import './LoginPage.css';

function LoginPage() {
  const { login, status } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  if (status === 'authenticated') {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault(); // stops the page from refreshing on submit
    setError('');
    setSubmitting(true);
    try {
      await login(username, password);
    } catch (err) {
      // 401 -> "Invalid username or password", 429 -> "Too many attempts. Try again in N minute(s)."
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      <ThemeToggle />
      <h1>ERP HR App</h1>
      <form onSubmit={handleSubmit}>
        {error && <p role="alert">{error}</p>}

        <h1>Login</h1>
        <label htmlFor="username">Username</label>
        <input type="text" id="username" autoComplete="username" value={username}
               onChange={(e) => setUsername(e.target.value)} required />

        <label htmlFor="password">Password</label>
        <input type="password" id="password" autoComplete="current-password" value={password}
               onChange={(e) => setPassword(e.target.value)} required />

        <button type="submit" disabled={submitting}>{submitting ? 'Signing in…' : 'Login'}</button>
      </form>
      <h4>@2026 Your Company. All rights reserved.</h4>
    </div>
  );
}

export default LoginPage;
