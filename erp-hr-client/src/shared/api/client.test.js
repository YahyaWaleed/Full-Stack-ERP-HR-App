import { afterEach, describe, expect, it, vi } from 'vitest';
import { fakeServer } from '../../test/fakeServer';
import { api, ApiError, setSessionExpiredHandler } from './client';
import { clearToken, getToken, setToken } from './tokenStorage';

afterEach(() => {
  clearToken();
  setSessionExpiredHandler(null);
});

describe('api client', () => {
  it('sends the access token and parses JSON', async () => {
    const { calls } = fakeServer({ 'GET /employees/1': () => ({ id: 1, fullNameEn: 'Ahmed' }) });
    setToken('access-1');

    await expect(api.get('/employees/1')).resolves.toEqual({ id: 1, fullNameEn: 'Ahmed' });
    expect(calls[0].headers.Authorization).toBe('Bearer access-1');
  });

  it('on 401 refreshes the session once and retries with the new token', async () => {
    let attempts = 0;
    const { calls } = fakeServer({
      'GET /leaves': ({ init }) => {
        attempts += 1;
        return init.headers.Authorization === 'Bearer fresh' ? { content: [] } : { status: 401, body: {} };
      },
      'POST /auth/refresh': () => ({ token: 'fresh', username: 'admin', role: 'HR_ADMIN' }),
    });
    setToken('expired');

    await expect(api.get('/leaves')).resolves.toEqual({ content: [] });
    expect(attempts).toBe(2);
    expect(getToken()).toBe('fresh');
    expect(calls.filter((c) => c.path === '/auth/refresh')).toHaveLength(1);
  });

  it('parallel 401s share a single refresh call', async () => {
    const { calls } = fakeServer({
      'GET /a': ({ init }) => (init.headers.Authorization === 'Bearer fresh' ? 'a' : { status: 401, body: {} }),
      'GET /b': ({ init }) => (init.headers.Authorization === 'Bearer fresh' ? 'b' : { status: 401, body: {} }),
      'POST /auth/refresh': async () => {
        await new Promise((r) => setTimeout(r, 10));
        return { token: 'fresh' };
      },
    });
    setToken('expired');

    await expect(Promise.all([api.get('/a'), api.get('/b')])).resolves.toEqual(['a', 'b']);
    expect(calls.filter((c) => c.path === '/auth/refresh')).toHaveLength(1);
  });

  it('ends the session when the refresh token is also gone', async () => {
    fakeServer({
      'GET /employees': () => ({ status: 401, body: {} }),
      'POST /auth/refresh': () => ({ status: 401, body: undefined }),
    });
    const expired = vi.fn();
    setSessionExpiredHandler(expired);
    setToken('expired');

    await expect(api.get('/employees')).rejects.toMatchObject({ status: 401 });
    expect(expired).toHaveBeenCalledOnce();
    expect(getToken()).toBeNull();
  });

  it('a 403 is a permission error, not a logout', async () => {
    const { calls } = fakeServer({ 'GET /reports/bank-transfer': () => ({ status: 403, body: { message: 'Forbidden' } }) });
    const expired = vi.fn();
    setSessionExpiredHandler(expired);

    const error = await api.get('/reports/bank-transfer').catch((e) => e);
    expect(error).toBeInstanceOf(ApiError);
    expect(error.status).toBe(403);
    expect(error.message).toMatch(/permission/);
    expect(expired).not.toHaveBeenCalled();
    expect(calls.some((c) => c.path === '/auth/refresh')).toBe(false);
  });

  it('exposes the backend message and field errors', async () => {
    fakeServer({
      'POST /employees': () => ({
        status: 400,
        body: { message: 'Invalid request', fieldErrors: ['nationalId: must be exactly 14 digits'] },
      }),
    });
    const error = await api.post('/employees', {}).catch((e) => e);
    expect(error.status).toBe(400);
    expect(error.fieldErrors).toEqual(['nationalId: must be exactly 14 digits']);
  });
});
