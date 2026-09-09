import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ThemeToggle from '../components/ThemeToggle';

// any CSS styling is completely done by AI //


function LoginPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    // function to handle the form by using the backend API
     const handleSubmit = async (e) => {
    e.preventDefault(); // stops the page from refreshing on submit

    try {
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        throw new Error('Invalid username or password');
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      localStorage.setItem('username', username);
      localStorage.setItem('role', data.role);
      navigate('/dashboard');

    } catch (err) {
      setError(err.message);
    }
  };

    
    return(
        <> 

        <ThemeToggle />
        <style>
            {`
                /* ============================================================
                LOGIN PAGE
                ============================================================ */

                body {
                    background:
                        radial-gradient(
                            circle at top left,
                            rgba(37, 99, 235, 0.08),
                            transparent 35%
                        ),
                        radial-gradient(
                            circle at bottom right,
                            rgba(37, 99, 235, 0.06),
                            transparent 35%
                        ),
                        var(--color-bg);
                    color: var(--color-text);
                }

                /* ============================================================
                LOGIN FORM CARD
                ============================================================ */

                form {
                    width: 380px;
                    max-width: calc(100% - 40px);
                    margin: 100px auto 20px;
                    padding: 35px 40px;
                    box-sizing: border-box;

                    background:
                        linear-gradient(
                            145deg,
                            var(--color-surface),
                            var(--color-bg)
                        );

                    border: 1px solid var(--color-border);
                    border-radius: 14px;

                    box-shadow: var(--shadow-card);

                    position: relative;
                    overflow: hidden;
                }

                /* Blue accent at the top of the card */

                form::before {
                    content: "";
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 4px;

                    background:
                        linear-gradient(
                            90deg,
                            #2563eb,
                            #3b82f6,
                            #60a5fa
                        );
                }

                /* ============================================================
                PAGE TITLE
                ============================================================ */

                body > #root > h1 {
                    color: var(--color-text);
                }

                /* ============================================================
                LOGIN HEADING
                ============================================================ */

                form h1 {
                    margin: 0 0 28px;

                    color: var(--color-text);

                    font-size: 1.8rem;
                    font-weight: 800;
                    text-align: center;

                    letter-spacing: -0.02em;
                }

                /* ============================================================
                LABELS
                ============================================================ */

                form label {
                    display: block;

                    margin-bottom: 7px;

                    color: var(--color-text);

                    font-size: 0.88rem;
                    font-weight: 700;
                }

                /* ============================================================
                INPUTS
                ============================================================ */

                form input {
                    width: 100%;
                    box-sizing: border-box;

                    padding: 11px 13px;
                    margin-bottom: 4px;

                    border: 1px solid var(--color-border);
                    border-radius: 8px;

                    background: var(--color-bg);
                    color: var(--color-text);

                    font-family: inherit;
                    font-size: 0.95rem;

                    outline: none;

                    transition:
                        border-color 0.2s ease,
                        box-shadow 0.2s ease,
                        background 0.2s ease,
                        transform 0.2s ease;
                }

                form input::placeholder {
                    color: var(--color-text-muted);
                }

                form input:hover {
                    border-color: var(--color-text-muted);
                }

                form input:focus {
                    border-color: var(--color-primary);

                    box-shadow:
                        0 0 0 3px rgba(37, 99, 235, 0.12);
                }

                /* ============================================================
                LOGIN BUTTON
                ============================================================ */

                form button {
                    width: 100%;

                    margin-top: 14px;
                    padding: 11px 16px;

                    border: 1px solid #1d4ed8;
                    border-radius: 8px;

                    background:
                        linear-gradient(
                            135deg,
                            #2563eb,
                            #1d4ed8
                        );

                    color: #ffffff;

                    font-family: inherit;
                    font-size: 0.95rem;
                    font-weight: 700;

                    cursor: pointer;

                    box-shadow:
                        0 4px 10px rgba(37, 99, 235, 0.22),
                        inset 0 1px 0 rgba(255, 255, 255, 0.15);

                    transition:
                        transform 0.2s ease,
                        box-shadow 0.2s ease,
                        background 0.2s ease;
                }

                form button:hover {
                    background:
                        linear-gradient(
                            135deg,
                            #1d4ed8,
                            #1e40af
                        );

                    transform: translateY(-2px);

                    box-shadow:
                        0 7px 16px rgba(37, 99, 235, 0.28),
                        inset 0 1px 0 rgba(255, 255, 255, 0.15);
                }

                form button:active {
                    transform: translateY(0);

                    box-shadow:
                        0 3px 7px rgba(37, 99, 235, 0.2);
                }

                /* ============================================================
                ERROR MESSAGE
                ============================================================ */

                form p {
                    margin: 0 0 18px;
                    padding: 10px 12px;

                    border: 1px solid var(--color-danger);
                    border-radius: 7px;

                    background: var(--color-danger-bg);
                    color: var(--color-danger) !important;

                    font-size: 0.88rem;
                    font-weight: 600;
                    text-align: center;
                }

                /* ============================================================
                FOOTER
                ============================================================ */

                form + h4 {
                    margin: 0 auto;

                    text-align: center;

                    color: var(--color-text-muted);

                    font-size: 0.75rem;
                    font-weight: 500;
                }

                /* ============================================================
                DARK MODE
                ============================================================ */

                [data-theme="dark"] form {
                    background:
                        linear-gradient(
                            145deg,
                            #24272d,
                            #1e2126
                        );

                    border-color: #363a42;

                    box-shadow:
                        0 12px 35px rgba(0, 0, 0, 0.35),
                        0 2px 8px rgba(0, 0, 0, 0.25);
                }

                [data-theme="dark"] form input {
                    background: #181b20;
                    border-color: #3b4049;
                    color: #e5e7eb;
                }

                [data-theme="dark"] form input:hover {
                    border-color: #4b5563;
                }

                [data-theme="dark"] form input:focus {
                    background: #1c2026;
                    border-color: #60a5fa;

                    box-shadow:
                        0 0 0 3px rgba(96, 165, 250, 0.15);
                }

                [data-theme="dark"] form label {
                    color: #d1d5db;
                }

                [data-theme="dark"] form h1 {
                    color: #e5e7eb;
                }

                [data-theme="dark"] form p {
                    background: #2a1719;
                    border-color: #5b2529;
                    color: #f87171 !important;
                }

                [data-theme="dark"] form + h4 {
                    color: #6b7280;
                }

                /* ============================================================
                RESPONSIVE
                ============================================================ */

                @media (max-width: 500px) {
                    form {
                        margin-top: 60px;
                        padding: 30px 25px;
                    }

                    form h1 {
                        font-size: 1.6rem;
                    }
                }
            `}
        </style>

            <h1 style={{ textAlign: 'center' }}>ERP HR App</h1>
            <form onSubmit={handleSubmit}>
                {error && <p style={{ color: 'red' }}>{error}</p>}

                <h1>Login</h1>
                <label htmlFor="username">Username</label>
                <input type="text" id="username" value={username} onChange={(e) => setUsername(e.target.value)} />
                <br></br> <br></br>
                <label htmlFor="password">Password</label>
                <input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                <br></br> <br></br>
                <button type="submit">Login</button>
            </form>
            <h4>@2026 Your Company. All rights reserved.</h4>
        </>
    );
}

export default LoginPage;