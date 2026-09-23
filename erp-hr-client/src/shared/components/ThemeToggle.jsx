import { useState, useEffect } from 'react';

function ThemeToggle() {
  const [theme, setTheme] = useState(() => {
    const saved = localStorage.getItem('theme');
    if (saved) return saved;
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === 'light' ? 'dark' : 'light'));
  };

  const nextTheme = theme === 'light' ? 'dark' : 'light';

  return (
    <button
      className={`theme-toggle-switch ${theme}`}
      onClick={toggleTheme}
      type="button"
      role="switch"
      aria-checked={theme === 'dark'}
      aria-label={`Switch to ${nextTheme} mode`}
      title={`Switch to ${nextTheme} mode`}
    >
      {/* Static Background Sun Icon */}
      <span className="icon-wrapper sun-icon" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="4.5" />
          <line x1="12" y1="1" x2="12" y2="3.5" />
          <line x1="12" y1="20.5" x2="12" y2="23" />
          <line x1="4.22" y1="4.22" x2="6" y2="6" />
          <line x1="18" y1="18" x2="19.78" y2="19.78" />
          <line x1="1" y1="12" x2="3.5" y2="12" />
          <line x1="20.5" y1="12" x2="23" y2="12" />
          <line x1="4.22" y1="19.78" x2="6" y2="18" />
          <line x1="18" y1="6" x2="19.78" y2="4.22" />
        </svg>
      </span>

      {/* Sliding Handle containing the active icon */}
      <span className="toggle-handle" aria-hidden="true">
        {theme === 'light' ? (
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="white" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="12" cy="12" r="4.5" />
            <line x1="12" y1="1" x2="12" y2="3.5" />
            <line x1="12" y1="20.5" x2="12" y2="23" />
            <line x1="4.22" y1="4.22" x2="6" y2="6" />
            <line x1="18" y1="18" x2="19.78" y2="19.78" />
            <line x1="1" y1="12" x2="3.5" y2="12" />
            <line x1="20.5" y1="12" x2="23" y2="12" />
            <line x1="4.22" y1="19.78" x2="6" y2="18" />
            <line x1="18" y1="6" x2="19.78" y2="4.22" />
          </svg>
        ) : (
          <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="white" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
          </svg>
        )}
      </span>

      {/* Static Background Moon Icon */}
      <span className="icon-wrapper moon-icon" aria-hidden="true">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
        </svg>
      </span>
    </button>
  );
}

export default ThemeToggle;