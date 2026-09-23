import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import ProtectedRoute from './ProtectedRoute';
import { AuthContext } from './useAuth';

function renderAt(auth, adminOnly) {
  return render(
    <AuthContext.Provider value={auth}>
      <MemoryRouter initialEntries={['/dashboard/reports']}>
        <Routes>
          <Route path="/login" element={<p>login page</p>} />
          <Route path="/dashboard/reports" element={<ProtectedRoute adminOnly={adminOnly}><p>secret reports</p></ProtectedRoute>} />
        </Routes>
      </MemoryRouter>
    </AuthContext.Provider>,
  );
}

describe('ProtectedRoute', () => {
  it('sends anonymous visitors to the login page', () => {
    renderAt({ status: 'anonymous', isAdmin: false }, false);
    expect(screen.getByText('login page')).toBeTruthy();
  });

  it('waits for the session check instead of bouncing to login on reload', () => {
    renderAt({ status: 'loading', isAdmin: false }, false);
    expect(screen.getByText('Loading…')).toBeTruthy();
  });

  it('shows admin-only pages to HR_ADMIN', () => {
    renderAt({ status: 'authenticated', isAdmin: true }, true);
    expect(screen.getByText('secret reports')).toBeTruthy();
  });

  it('keeps HR_USER out of admin-only pages without logging them out', () => {
    renderAt({ status: 'authenticated', isAdmin: false }, true);
    expect(screen.queryByText('secret reports')).toBeNull();
    expect(screen.getByText(/don't have permission/)).toBeTruthy();
  });
});
