import { getToken, setToken, clearToken } from './tokenStorage';

// set VITE_API_URL (e.g. in .env.production) to point a build at another backend
export const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';

// an error from the API: status code, the backend's message and any per-field validation errors
export class ApiError extends Error {
  constructor(status, message, fieldErrors = []) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fieldErrors = fieldErrors || [];
  }
}

// AuthProvider registers its logout here so a session that can't be refreshed also clears the React state
let onSessionExpired = null;
export function setSessionExpiredHandler(handler) {
  onSessionExpired = handler;
}

// ---- auth calls (they carry the HttpOnly refresh cookie, hence credentials: 'include') ----------

async function authCall(path, body) {
  const response = await fetch(`${BASE_URL}/auth${path}`, {
    method: 'POST',
    credentials: 'include',
    headers: body ? { 'Content-Type': 'application/json' } : {},
    body: body ? JSON.stringify(body) : undefined,
  });
  return response;
}

export async function login(username, password) {
  const response = await authCall('/login', { username, password });
  if (!response.ok) {
    throw await toError(response);
  }
  const session = await response.json();
  setToken(session.token);
  return session;
}

// one refresh at a time: parallel requests that all hit a 401 share a single refresh call
let refreshing = null;
export function refreshSession() {
  if (!refreshing) {
    refreshing = authCall('/refresh')
      .then(async (response) => {
        if (!response.ok) {
          clearToken();
          return null;
        }
        const session = await response.json();
        setToken(session.token);
        return session;
      })
      .catch(() => null)
      .finally(() => {
        refreshing = null;
      });
  }
  return refreshing;
}

export async function logout() {
  try {
    await authCall('/logout');
  } finally {
    clearToken();
  }
}

// ---- everything else -----------------------------------------------------------------------

// request('/employees', { method: 'GET', signal }) -> parsed JSON (or null for empty bodies).
// A 401 triggers one silent refresh and a retry; if that fails the session is over.
export async function request(path, { method = 'GET', body, signal } = {}, retried = false) {
  const token = getToken();
  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    signal,
    headers: {
      ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (response.status === 401 && !retried) {
    const session = await refreshSession();
    if (session) {
      return request(path, { method, body, signal }, true);
    }
    onSessionExpired?.();
    throw new ApiError(401, 'Your session has expired. Please log in again.');
  }

  if (!response.ok) {
    throw await toError(response);
  }

  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

async function toError(response) {
  if (response.status === 403) {
    return new ApiError(403, "You don't have permission to perform this action.");
  }
  if (response.status === 429) {
    const wait = response.headers.get('Retry-After');
    return new ApiError(429, `Too many attempts. Try again in ${wait ? Math.ceil(Number(wait) / 60) : 'a few'} minute(s).`);
  }
  let message = `Request failed (${response.status})`;
  let fieldErrors = [];
  try {
    const parsed = JSON.parse(await response.text());
    message = parsed.message || message;
    fieldErrors = parsed.fieldErrors || [];
  } catch {
    // response wasn't JSON -- keep the default message
  }
  return new ApiError(response.status, message, fieldErrors);
}

export const api = {
  get: (path, options) => request(path, { ...options, method: 'GET' }),
  post: (path, body) => request(path, { method: 'POST', body }),
  put: (path, body) => request(path, { method: 'PUT', body }),
  patch: (path, body) => request(path, { method: 'PATCH', body }),
  delete: (path) => request(path, { method: 'DELETE' }),
};
